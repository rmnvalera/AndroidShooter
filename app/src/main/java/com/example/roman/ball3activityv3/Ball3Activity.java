package com.example.roman.ball3activityv3;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.webkit.PermissionRequest;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
//import com.karumi.dexter.Dexter;


public class Ball3Activity extends AppCompatActivity implements CvCameraViewListener2{

    private CameraBridgeViewBase mOpenCvCameraView;

    private int                 mViewMode;
    private static final int    VIEW_MODE_RGBA   = 0;
    private static final int    VIEW_MODE_GRAY   = 1;
    private static final int    VIEW_MODE_CANNY  = 2;
    private static final int    VIEW_MODE_FEATURES = 5;


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
        OpenCVLoader.initDebug();
        mOpenCvCameraView.enableView();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }
}
