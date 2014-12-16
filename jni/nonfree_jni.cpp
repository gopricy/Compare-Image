// A simple demo of JNI interface to implement SIFT detection for Android application using nonfree module in OpenCV4Android.
// Created by Guohui Wang 
// Email: robertwgh_at_gmail_com
// Data: 2/26/2014

#include <jni.h>
#include <android/log.h>

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/nonfree/features2d.hpp>
#include <opencv2/nonfree/nonfree.hpp>
#include <iostream>

using namespace cv;
using namespace std;

#define  LOG_TAG    "SIFT_RANSAC"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

#define CHECK_MAT(cond) if(!(cond)){ LOGI("FAILED: " #cond); return; }

#define MAXSIZE 512
#define RESIZE_FACTOR 0.125

typedef unsigned char uchar;

int get_SIFT(char * imgInFile, vector<KeyPoint>& keypoints, Mat& descriptors);
int get_MATCH(Mat descriptors1, Mat descriptors2, vector<DMatch>& matches);
void Mat_to_vector_KeyPoint(Mat& mat, vector<KeyPoint>& v_kp);
void vector_KeyPoint_to_Mat(vector<KeyPoint>& v_kp, Mat& mat);
void find_good_matches(vector<DMatch> matches, vector<DMatch>& good_matches);
void vector_DMatch_to_Mat(vector<DMatch>& matches, Mat& mat);

extern "C" {
    JNIEXPORT void JNICALL Java_com_example_testlocation_ComparePicture_getSIFT(JNIEnv * env, jobject, jstring addrImgInFile, jlong addrKeypoints, jlong addrDescriptors);
    JNIEXPORT void JNICALL Java_com_example_testlocation_ComparePicture_getMATCH(JNIEnv * env, jobject, jlong addrDescriptors1, jlong addrDescriptors2, jlong addrMatches);
    JNIEXPORT void JNICALL Java_com_example_testlocation_ComparePicture_getKeypointAndDescriptor(JNIEnv * env, jobject, jstring addrDataInFile, jlong addrKeypoints, jlong addrDescriptors);
};
// function to get the SIFT keypoints and descriptors from an image
JNIEXPORT void JNICALL Java_com_example_testlocation_ComparePicture_getSIFT(JNIEnv * env, jobject, jstring addrImgInFile, jlong addrKeypoints, jlong addrDescriptors)
{
	LOGI( "Start get variables! \n");
	// get variables

	// get string to char*
	const char* imgInFile_const = env->GetStringUTFChars(addrImgInFile, JNI_FALSE);
	char* imgInFile = new char[strlen(imgInFile_const)+1];
	strcpy(imgInFile, imgInFile_const);

	Mat& keypoints_mat = *(Mat*)addrKeypoints;
	vector<KeyPoint>* keypoints = new vector<KeyPoint>();
	Mat& descriptors = *(Mat*)addrDescriptors;
	// convert mat to vector keypoint
	LOGI( "%d \n", (int)keypoints_mat.cols);
	// Mat_to_vector_KeyPoint(keypoints_mat, *keypoints);
	LOGI( "Start get_SIFT! \n");
	get_SIFT(imgInFile, *keypoints, descriptors);
	LOGI( "End get_SIFT!\n");
	// convert vector keypoint back to mat
	vector_KeyPoint_to_Mat(*keypoints, keypoints_mat);

	// release the char*
	env->ReleaseStringUTFChars(addrImgInFile, imgInFile_const);

	// release the variables
	delete []imgInFile;
	delete keypoints;
}

// function to get matches between the descriptors of two image
JNIEXPORT void JNICALL Java_com_example_testlocation_ComparePicture_getMATCH(JNIEnv * env, jobject, jlong addrDescriptors1, jlong addrDescriptors2, jlong addrMatches)
{
	LOGI( "Start get matches! \n");

	// get mat file
	Mat& descriptors1 = *(Mat*)addrDescriptors1;
	Mat& descriptors2 = *(Mat*)addrDescriptors2;
	Mat& matches_mat = *(Mat*)addrMatches;
	vector<DMatch>* matches = new vector<DMatch>();

	// get matches
	LOGI( "Start get_MATCH! \n");
	get_MATCH(descriptors1, descriptors2, *matches);

	// get good matches
	vector<DMatch>* good_matches = new vector<DMatch>();
	find_good_matches(*matches, *good_matches);

	//convert vector dmatch to mat
	vector_DMatch_to_Mat(*good_matches, matches_mat);
	LOGI( "End get_MATCH! \n");

	// release the variables
	delete matches;
	delete good_matches;
}

// function to get the keypoints and descriptors in dataset image
JNIEXPORT void JNICALL Java_com_example_testlocation_ComparePicture_getKeypointAndDescriptor(JNIEnv * env, jobject, jstring addrDataInFile, jlong addrKeypoints, jlong addrDescriptors)
{
	LOGI( "Start get variables! \n");
	// get variables

	// get string to char*
	const char* dataInFile_const = env->GetStringUTFChars(addrDataInFile, JNI_FALSE);
	char* dataInFile = new char[strlen(dataInFile_const)+1];
	strcpy(dataInFile, dataInFile_const);

	LOGI( "dataInFile load finished.");

	// initialize mat file
	Mat& keypoints_mat = *(Mat*)addrKeypoints;
	vector<KeyPoint>* keypoints = new vector<KeyPoint>();
	Mat& descriptors = *(Mat*)addrDescriptors;

	LOGI( "keypoints and descriptors initialize finished.");

	// load the yml file from internal storage
	FileStorage fs(dataInFile, FileStorage::READ);
	FileNode key = fs["keypoint"];
	FileNode descript = fs["descriptor"];
	LOGI( "Read keypoints and descriptors.");
	read(key, *keypoints);
	read(descript, descriptors);
	fs.release();

	// convert vector keypoint to mat
	vector_KeyPoint_to_Mat(*keypoints, keypoints_mat);

	LOGI( "keypoints and descriptors load finished.");

	// release the variables
	delete []dataInFile;
	delete keypoints;
}

int get_SIFT(char * imgInFile, vector<KeyPoint>& keypoints, Mat& descriptors)
{
	//cv::initModule_nonfree();
	//cout <<"initModule_nonfree() called" << endl;

	// load the query image
	Mat query_image;
	LOGI("%s \n", imgInFile);
	query_image = imread(imgInFile, CV_LOAD_IMAGE_COLOR);
	if (!query_image.data)
	{
		LOGI("Could not open or find the image!\n");
		return -1;
	}

	// set variables
	//vector<KeyPoint> keypoints;
	//Mat descriptors;

	// Create a SIFT keypoint detector.
	SiftFeatureDetector detector;
	detector.detect(query_image, keypoints);
	LOGI("Detected %d keypoints\n", (int)keypoints.size());

	// Compute feature description.
	detector.compute(query_image, keypoints, descriptors);
	LOGI("Compute feature.\n");

//	// Store description to "descriptors.des".
//	FileStorage fs;
//	fs.open("descriptors.des", FileStorage::WRITE);
//	LOGI("Opened file to store the features.\n");
//	fs << "descriptors" << descriptors;
//	LOGI("Finished writing file.\n");
//	fs.release();
//	LOGI("Released file.\n");

//	// Show keypoints in the output image.
//	Mat outputImg;
//	Scalar keypointColor = Scalar(255, 0, 0);
//	drawKeypoints(query_image, keypoints, outputImg, keypointColor, DrawMatchesFlags::DRAW_RICH_KEYPOINTS);
//	LOGI("Drew keypoints in output image file.\n");

//#ifdef WIN32
//	namedWindow("Output image", CV_WINDOW_AUTOSIZE);
//	imshow("Output image", outputImg);
//	waitKey(0);
//#endif

//	LOGI("Generate the output image.\n");
//	imwrite(imgOutFile, outputImg);

	LOGI("Done.\n");
	return 0;
}

// function to match the two descriptors from two image, and output the matches
int get_MATCH(Mat descriptors1, Mat descriptors2, vector<DMatch>& matches){
	FlannBasedMatcher matcher;
	matcher.match(descriptors1, descriptors2, matches);
	return 0;
}

// C++ / JNI
// vector_KeyPoint converters

void Mat_to_vector_KeyPoint(Mat& mat, vector<KeyPoint>& v_kp)
{
    v_kp.clear();
    CHECK_MAT((mat.type()==CV_32FC(7) && mat.cols==1) || (mat.dims==0));
    for(int i=0; i<mat.rows; i++)
    {
        Vec<float, 7> v = mat.at< Vec<float, 7> >(i, 0);
        KeyPoint kp(v[0], v[1], v[2], v[3], v[4], (int)v[5], (int)v[6]);
        v_kp.push_back(kp);
    }
    return;
}

void vector_KeyPoint_to_Mat(vector<KeyPoint>& v_kp, Mat& mat)
{
    int count = (int)v_kp.size();
    mat.create(count, 1, CV_32FC(7));
    for(int i=0; i<count; i++)
    {
        KeyPoint kp = v_kp[i];
        mat.at< Vec<float, 7> >(i, 0) = Vec<float, 7>(kp.pt.x, kp.pt.y, kp.size, kp.angle, kp.response, (float)kp.octave, (float)kp.class_id);
    }
}

// get good matches
void find_good_matches(vector<DMatch> matches, vector<DMatch>& good_matches){
	double min = 200.0;
	// get good matches
	for (int i = 0; i < matches.size(); i++){
		if (matches[i].distance < min){
			good_matches.push_back(matches[i]);
		}
	}
}

// convert DMatch to Coodinate
void vector_DMatch_to_Mat(vector<DMatch>& matches, Mat& mat)
{
	int length = matches.size();
	mat.create(length, 1, CV_32FC(4));
	for (int i = 0; i < length; i++)
	{
		DMatch dm = matches[i];
		mat.at< Vec<float, 4> >(i, 0) = Vec<float, 4>((float)dm.queryIdx, (float)dm.trainIdx, (float)dm.imgIdx, dm.distance);
	}
}
