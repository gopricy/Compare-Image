package com.example.testlocation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author Zhou Yiren (SUTD) 2014
 * 
 *
 */
public class ShowPicture extends ActionBarActivity {

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
	
	private static final String TAG = null;
	private int maxWidth = 128;
	private int maxHeight = 128;
	
	// set in sample size to a valid value
	public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
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
				turnToShowPicture.setClass(ShowPicture.this, ShowPicture.class);
				ShowPicture.this.startActivity(turnToShowPicture);
				break;
			}

			default:
				break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}
	
	// go to next activity to compare photos
	private void locatePictureIntent() {
		//turn to compare picture activity
		Intent turnToComparePicture = new Intent();
		turnToComparePicture.setClass(ShowPicture.this, ComparePicture.class);
		ShowPicture.this.startActivity(turnToComparePicture);
	}
		
	//When press takePicture
	Button.OnClickListener takePicOnClickListener = 
			new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
			}
		};
		
	/*When press LoadPicture button*/
	Button.OnClickListener locPicOnClickListener = 
			new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				locatePictureIntent();
			}
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_picture1);

		//get just taken image directory
		String currentPath = ((GlobalVariables)getApplication()).getPath();
		String currentName = ((GlobalVariables)getApplication()).getName();
		
//		// get taken image directory
//		File imagesFolder = new File(Environment.getExternalStorageDirectory(),
//				"TestLocation");
		
		// load the image just taken
//		File imgFile = new File(imagesFolder, "query_image.jpg");
		File imgFile = new File(currentPath, currentName);
		if (imgFile.exists()) {
			// load image information
			final Options options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(
					imgFile.getAbsolutePath(), options);
			// calculate image in sample size
			options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
			options.inJustDecodeBounds = false;

			Bitmap myBitmap = BitmapFactory.decodeFile(
					imgFile.getAbsolutePath(), options);
			
			// save the small image back to the directory
			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
			myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputBuffer);
		    byte[] byteImage1=outputBuffer.toByteArray();
		    /*save file to internal storage*/
		    try {
		    	String currentNameSmall = ((GlobalVariables)getApplication()).getNameSmall();
		    	File imgFileSmall = new File(currentPath, currentNameSmall);
		       FileOutputStream outputStream = new FileOutputStream(imgFileSmall.getAbsolutePath());
		      outputStream.write(byteImage1);
		      outputStream.flush();
		      outputStream.close(); 
		      /*By using this line you can able to see saved images in the gallery view.*/
//		      sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		    } catch (Exception e) {
		      e.printStackTrace();
		    }

		    // set the bitmap to image view
			ImageView myImage = (ImageView) findViewById(R.id.imageView2);
			myImage.setImageBitmap(myBitmap);
			myImage.setVisibility(View.VISIBLE);
		}
		
		/*When press takePicture button*/
		Button picBtn = (Button) findViewById(R.id.Button_take_again);
		setBtnListenerOrDisable(picBtn, takePicOnClickListener,MediaStore.ACTION_IMAGE_CAPTURE);
		
		//when press getLocation button
		Button locBtn = (Button) findViewById(R.id.Button_compare);
		locBtn.setOnClickListener(locPicOnClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_picture1, menu);
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
	
//	// function to take picture
//	public void takePicture(View view) {
//		Intent intent = new Intent(this, TakePicture1.class);
//		startActivity(intent);
//	}
	
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
