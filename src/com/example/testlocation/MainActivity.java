package com.example.testlocation;

//import com.example.cancerdiagnosis1.R;

import java.io.File;
import java.util.List;

import com.example.testlocation.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * @author Zhou Yiren (SUTD) 2014
 * 
 *
 */
public class MainActivity extends ActionBarActivity {
	
	private static final int ACTION_TAKE_PHOTO_B = 1;
	
	private String currentPhotoPath;
	private String currentPhotoName;

	/* This part is for store TestLocation folder in different phone */
	// for HTC One X
//	private static final String INTERNAL_STORAGE_STRING = "/mnt/emmc";
	// for other phone
	private static final String INTERNAL_STORAGE_STRING = Environment.getExternalStorageDirectory().toString();
	/* ------------------------------------------------------------- */
	
//	private static final String JPEG_FILE_PREFIX = "IMG_";
//	private static final String FILE_TYPE = ".jpg"; 
//	private AlbumStorageDirFactory albumStorageDirFactory = null;
	
	// set photo path and name, take picture, and go to show picture 
	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);		
		switch(actionCode) 
		{
			case ACTION_TAKE_PHOTO_B: 
			{
				// set path for store the taken image
				File imagesFolder = new File(INTERNAL_STORAGE_STRING,
						"TestLocation");
				currentPhotoPath = imagesFolder.getAbsolutePath();
				File f = new File(imagesFolder, "query_image.jpg");
				currentPhotoName = "query_image.jpg";
				// set global variables
				((GlobalVariables)getApplication()).setPath(currentPhotoPath);
				((GlobalVariables)getApplication()).setName(currentPhotoName);
				((GlobalVariables)getApplication()).setNameSmall("query_image_small.jpg");
				/*If the EXTRA_OUTPUT is not present, then a small sized image is returned as a Bitmap object 
				in the extra field. If the EXTRA_OUTPUT is present, then the full-sized image will be written 
				to the Uri value of EXTRA_OUTPUT.
				*/
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				//turn to show picture activity
				Intent turnToShowPicture = new Intent();
				turnToShowPicture.setClass(MainActivity.this, ShowPicture.class);
				MainActivity.this.startActivity(turnToShowPicture);
				break;
			}

			default:
				break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}
	
	//When press takePicture
	Button.OnClickListener mTakePicOnClickListener = 
			new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
			}
		};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        /*When press takePicture button*/
		Button picBtn = (Button) findViewById(R.id.takePicture);
		setBtnListenerOrDisable(picBtn, mTakePicOnClickListener,MediaStore.ACTION_IMAGE_CAPTURE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 * 
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
    // function to control button
    private void setBtnListenerOrDisable( Button btn, Button.OnClickListener onClickListener,String intentName) 
	{
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
}
