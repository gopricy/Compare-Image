package com.example.testlocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;

import javax.xml.parsers.*;

import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Zhou Yiren (SUTD) 2014
 * 
 *
 */

// extends ActionBarActivity 
public class ComparePicture extends ActionBarActivity{
	// load the .so file for SIFT model
	private static final String INTERNAL_STORAGE_STRING = Environment.getExternalStorageDirectory().toString();
	private String currentPhotoPath;
	static 
	{
		try
		{
			/**
			 * Load OpenCV library and SIFT matching code from nonfree module
			 */
			// Load necessary libraries.
			System.loadLibrary("opencv_java");
			System.loadLibrary("nonfree");
			System.loadLibrary("nonfree_jni");
		}
		catch( UnsatisfiedLinkError e )
		{
			System.err.println("Native code library failed to load.\n" + e);
		}
	}

	/* TODO part: 
	 * define variables
	 */
	MatOfKeyPoint Keypoint_Input = new MatOfKeyPoint(); 
	Mat Descriptor_Input = new Mat(); 
	
	private void parseXmlFile(){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			Log.e("XML", "Start");
			//parse using builder to get DOM representation of the XML file
			AssetManager assetManager = getAssets();
			
			XmlResourceParser xrp = assetManager.openXmlResourceParser("image_data1");
			Log.e("XML", xrp.toString());
			
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

//	
//	public static void main(String[] args) {
//		getSIFT("res/drawable-ldpi-v4/sketch.jpg",a,b);
//		System.out.println(a);
//		System.out.println(b);
//	}

	
	// extract the needed coordinate
	/**
	 * Method to extract the coordinates of matched points
	 * @param kp1 SIFT keypoint in the first image
	 * @param kp2 SIFT keypoint in the second image
	 * @param matches match index of 2 images
	 * @param arraylist1 coordinates of matched points in the first image
	 * @param arraylist2 coordinates of matched points in the second image
	 */
	void extractMatchCoordinate(MatOfKeyPoint kp1, MatOfKeyPoint kp2, MatOfDMatch matches, ArrayList<Point> arraylist1, ArrayList<Point> arraylist2){
		/* TODO part:
		 * fill up the function code
		 */
		
		
		
	}

	// add all the 2-D array(with same size) in the list to one single 2-D array
	private double[][] get_whole_matrix(List<double[][]> list){
		double[][] array = new double[][]{};
		List<double[]> list_temp = new ArrayList<double[]>();
		for (int i = 0; i < list.size(); i++){
			list_temp.addAll(Arrays.<double[]> asList(list.get(i)));
		}
		return list_temp.toArray(array);
	}

	// convert 1-D array to 2-D array
	private double[][] oneDtwoD(double[] a, int m, int n){
		if (a.length != m*n){
			throw new IllegalArgumentException("Number of 1-D array and 2-D array elements don't match!");
		}
		double[][] A = new double[m][n];
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				A[i][j] = a[j*n + i%n];
			}
		}
		return A;
	}

	/* TODO part:
	 * private functions that are needed for RANSAC
	 */

	// get homography score
	/**
	 * Method to get homography score based on difference value;
	 * if difference is less than threshold, consider it's match
	 * @param x1 vectors for first image
	 * @param x2 vectors for second image
	 * @param threshold value for determine whether match or not
	 * @return number of match
	 */
	int score_homography(double[][] x1, double[][] x2, int threshold){
		/* TODO part:
		 * function to compute number of RANSAC match points
		 */	
		return 0;

	}


	// get transformation matrix
	/**
	 * Method to estimate the transformation matrix
	 * @param num_choose number of chosen points
	 * @param X1_chosen chosen points from first image
	 * @param X2_chosen chosen points from second image
	 * @return H transformation matrix
	 */
	private double[][] estimateGeoTransMat(int num_choose, double[][] X1_chosen, double[][] X2_chosen) // return H
	{
		// get variables
		double[][] x1 = new double[1][3];
		double[][] x2 = new double[3][3];
		// initialize the list_A
		List<double[][]> list_A = new ArrayList<double[][]>();

		// get the random selected point pairs
		for (int j = 0; j < num_choose; j++){
			// x1
			x1[0][0] = X1_chosen[0][j];	x1[0][1] = X1_chosen[1][j];	x1[0][2] = X1_chosen[2][j];
			// skew symmetric matrix of x2
			x2[0][0] = 0;	x2[0][1] = -X2_chosen[2][j];	x2[0][2] = X2_chosen[1][j];
			x2[1][0] = X2_chosen[2][j];	x2[1][1] = 0;	x2[1][2] = -X2_chosen[0][j];
			x2[2][0] = -X2_chosen[1][j];	x2[2][1] = X2_chosen[0][j];	x2[2][2] = 0;
			// calculate matrix list_A{j}
			list_A.add(KroneckerOperation.product(x1, x2));
		}

		// get matrix A
		double[][] A = get_whole_matrix(list_A);
		Log.i("Size of A:", "	" + A.length + "!!!" + A[0].length);
		// convert A to matrix
		SimpleMatrix matA = new SimpleMatrix(A);
		// get SVD of A
		SimpleSVD s =matA.svd();
		// get matrix V
		SimpleMatrix V = s.getV();
		// convert back to array
		double[] v_1D = V.getMatrix().getData();
		double[][] v = oneDtwoD(v_1D, 9, 9);

		// only choose the last row
		double[] v9 = new double[9];
		for (int j = 0; j < 9; j++){
			v9[j] = v[8][j];
		}

		// get matrix H
		double[][] H = oneDtwoD(v9, 3, 3);
		Log.i("Size of H:", "	" + H.length + "!!!" + H[0].length);
		return H;
	}

	// using RANSAC to compare the two matched keypoints
	/**
	 * Method for apply RANSAC
	 * @param AL1 coordinate list of matched points in the first image
	 * @param AL2 coordinate list of matched points in the second image
	 * @param maxTime number of iteration times
	 * @return number of match after RANSAC
	 */
	int RANSAC_match(ArrayList<Point> AL1, ArrayList<Point> AL2, int maxTime){
		/* TODO part:
		 * RANSAC function
		 */
		return 0;

	}


	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/* TODO part:
		 * compare the query image with all dataset images, and get the best match image to show
		 */
		super.onCreate(savedInstanceState);
		parseXmlFile();
		File imagesFolder = new File(INTERNAL_STORAGE_STRING,
				"TestLocation");
		File f = new File(imagesFolder, "query_image_small.jpg");
		getSIFT(f.getAbsolutePath(), Keypoint_Input.getNativeObjAddr(), Descriptor_Input.getNativeObjAddr());
		//Log.e("SIFT", Keypoint_Input.toString() + Descriptor_Input.toString());
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_picture1, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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

	/*JNI functions
	 * we use this to call functions inside jni folder
	 */
	/**
	 * Method to call JNI function: getSIFT;
	 * Get SIFT keypoints and descriptors
	 * @param addrImage address of input image
	 * @param addrKeypoint address of output keypoints
	 * @param addrDescriptor address of output descriptors
	 */
	public static native void getSIFT(String addrImage, long addrKeypoint, long addrDescriptor);
	/**
	 * Method to call JNI function: getMATCH;
	 * Get matches between SIFT points in both images
	 * @param addrDescriptor1 address of descriptors for the first image
	 * @param addrDescriptor2 address of descriptors for the second image
	 * @param addrMatch address of matching index
	 */
	public static native void getMATCH(long addrDescriptor1, long addrDescriptor2, long addrMatch);
	/**
	 * Method to call JNI function: getKeypointAndDescriptor;
	 * Load SIFT keypoints and descriptors of dataset image
	 * @param addrData address of stored data of dataset image
	 * @param addrKeypoint address of output keypoints
	 * @param addrDescriptoraddress of output descriptors
	 */
	public static native void getKeypointAndDescriptor(String addrData, long addrKeypoint, long addrDescriptor);
}
