package com.example.roman.ball3activityv3;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.webkit.PermissionRequest;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
//import com.karumi.dexter.Dexter;


public class Ball3Activity extends AppCompatActivity implements CvCameraViewListener2{
    private static final String  TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;

    private int                 mViewMode;
    private static final int    VIEW_MODE_RGBA   = 0;
    private static final int    VIEW_MODE_GRAY   = 1;
    private static final int    VIEW_MODE_CANNY  = 2;
    private static final int    VIEW_MODE_FEATURES = 5;

    private Mat          mRgba;
    private Mat          mIntermediateMat;
    private Mat          mGray;
    private Mat          mHSV;
    private Mat          mThresholded;
    private Mat          mThresholded2;
    private Mat          array255;
    private Mat          distance;

    // Initialize OpenCV manager.
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // если хотим, чтобы приложение постоянно имело портретную ориентацию
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // если хотим, чтобы приложение было полноэкранным
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.surface_view);
        mOpenCvCameraView = findViewById(R.id.view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.RGBA :
                Log.i("Menu:", "RGBA");
                mViewMode = VIEW_MODE_RGBA;
                return true;
            case R.id.HSV:
                Log.i("Menu:", "HSV");
                mViewMode = VIEW_MODE_GRAY;
                return true;
            case R.id.Thresholded:
                Log.i("Menu:", "Thresholded");
                mViewMode = VIEW_MODE_CANNY;
                return true;
            case R.id.Ball:
                Log.i("Menu:", "Ball");
                mViewMode = VIEW_MODE_FEATURES;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        OpenCVLoader.initDebug();
//        mOpenCvCameraView.enableView();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mHSV = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        array255=new Mat(height,width,CvType.CV_8UC1);
        distance=new Mat(height,width,CvType.CV_8UC1);
        mThresholded=new Mat(height,width,CvType.CV_8UC1);
        mThresholded2=new Mat(height,width,CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        final int viewMode = mViewMode;
        mRgba = inputFrame.rgba();
        if (viewMode==VIEW_MODE_RGBA) return mRgba;
        List<Mat> lhsv = new ArrayList<Mat>(3);
        Mat circles = new Mat(); // No need (and don't know how) to initialize it.
        // The function later will do it... (to a 1*N*CV_32FC3)
        array255.setTo(new Scalar(255));
        Scalar hsv_min = new Scalar(18, 50, 50, 0);
        Scalar hsv_max = new Scalar(20, 255, 255, 0);
        Scalar hsv_min2 = new Scalar(20, 50, 50, 0);
        Scalar hsv_max2 = new Scalar(30, 255, 255, 0);
        //double[] data=new double[3];
        // One way to select a range of colors by Hue
        Imgproc.cvtColor(mRgba, mHSV, Imgproc.COLOR_RGB2HSV,4);
        if (viewMode==VIEW_MODE_GRAY) return mHSV;
        Core.inRange(mHSV, hsv_min, hsv_max, mThresholded);
        Core.inRange(mHSV, hsv_min2, hsv_max2, mThresholded2);
        Core.bitwise_or(mThresholded, mThresholded2, mThresholded);


        Core.split(mHSV, lhsv); // We get 3 2D one channel Mats
        Mat S = lhsv.get(1);
        Mat V = lhsv.get(2);
        Core.subtract(array255, S, S);
        Core.subtract(array255, V, V);
        S.convertTo(S, CvType.CV_32F);
        V.convertTo(V, CvType.CV_32F);
        Core.magnitude(S, V, distance);
        Core.inRange(distance,new Scalar(0.0), new Scalar(200.0), mThresholded2);
        Core.bitwise_and(mThresholded, mThresholded2, mThresholded);


        // Apply the Hough Transform to find the circles
        Imgproc.GaussianBlur(mThresholded, mThresholded, new Size(9,9),0,0);
        Imgproc.HoughCircles(mThresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, mThresholded.height()/4, 500, 50, 0, 0);
        if (viewMode==VIEW_MODE_CANNY){
            Imgproc.Canny(mThresholded, mThresholded, 500, 250); // This is not needed.
            // It is just for display
            Imgproc.cvtColor(mThresholded, mRgba, Imgproc.COLOR_GRAY2RGB, 4);
            return mRgba;
        }
        //int cols = circles.cols();
        int rows = circles.rows();
        int elemSize = (int)circles.elemSize(); // Returns 12 (3 * 4bytes in a float)
        float[] data2 = new float[rows * elemSize/4];
        if (data2.length>0){
            circles.get(0, 0, data2); // Points to the first element and reads the whole thing
            // into data2
            for(int i=0; i<data2.length; i=i+3) {
                Point center= new Point(data2[i], data2[i+1]);
                Imgproc.ellipse( mRgba, center, new Size((double)data2[i+2], (double)data2[i+2]), 0, 0, 360, new Scalar( 255, 0, 255 ), 4, 8, 0 );
            }
        }
        return mRgba;
    }
}
