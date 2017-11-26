package com.example.kollins.libras_android;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.kollins.libras_android.camera_management.CameraSettings;

import org.opencv.android.CameraBridgeViewBase;

/**
 * Created by kollins on 15/08/17.
 */

public class CameraView extends FragmentActivity {

//    private CameraSettings cameraSet;
    private CameraBridgeViewBase mOpenCvCameraView;
    private RadioGroup rCamera;

    public SeekBar hmin,smin,vmin,hmax,smax,vmax;
    public SeekBar threshold1,threshold2;
    public SeekBar minNeighbors;

    public static int hminProgress = 100,
            sminProgress = 63,
            vminProgress = 57,
            hmaxProgress = 255,
            smaxProgress = 255,
            vmaxProgress = 255;

    public static int threshold1Progress = 100,
            threshold2Progress = 100;

    public static int minNeighborsProgress = 3;

    private LinearLayout layoutBorder, layoutSkin, layoutFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MainActivity.cSettings.setCongigView("NONE");
//        cameraSet = new CameraSettings("NONE");

        rCamera = (RadioGroup) findViewById(R.id.radioCamera);
        rCamera.setOnCheckedChangeListener(cameraConfig(MainActivity.cSettings));

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(MainActivity.cSettings);
        mOpenCvCameraView.enableView();

        layoutBorder = (LinearLayout) findViewById(R.id.layoutBorder);
        layoutSkin = (LinearLayout) findViewById(R.id.layoutSkin);
        layoutFace = (LinearLayout) findViewById(R.id.layoutFace);

        threshold1 = (SeekBar) findViewById(R.id.threshold1);
        threshold2 = (SeekBar) findViewById(R.id.threshold2);

        threshold1.setProgress(100);
        threshold2.setProgress(100);

        threshold1.setOnSeekBarChangeListener(thresh1Listener());
        threshold2.setOnSeekBarChangeListener(thresh2Listener());

        hmin = (SeekBar) findViewById(R.id.hmin);
        smin = (SeekBar) findViewById(R.id.smin);
        vmin = (SeekBar) findViewById(R.id.vmin);
        hmax = (SeekBar) findViewById(R.id.hmax);
        smax = (SeekBar) findViewById(R.id.smax);
        vmax = (SeekBar) findViewById(R.id.vmax);

        hmin.setProgress(103);
        smin.setProgress(0);
        vmin.setProgress(100);
        hmax.setProgress(180);
        smax.setProgress(100);
        vmax.setProgress(255);

        hmin.setOnSeekBarChangeListener(hminListener());
        smin.setOnSeekBarChangeListener(sminListener());
        vmin.setOnSeekBarChangeListener(vminListener());
        hmax.setOnSeekBarChangeListener(hmaxListener());
        smax.setOnSeekBarChangeListener(smaxListener());
        vmax.setOnSeekBarChangeListener(vmaxListener());

        minNeighbors = (SeekBar) findViewById(R.id.minN);

        minNeighbors.setProgress(3);

        minNeighbors.setOnSeekBarChangeListener(minNListener());

    }

    private SeekBar.OnSeekBarChangeListener thresh1Listener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                threshold1Progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
    private SeekBar.OnSeekBarChangeListener thresh2Listener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                threshold2Progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private SeekBar.OnSeekBarChangeListener hminListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hminProgress = i;
                Log.i("Config","hmin: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
    private SeekBar.OnSeekBarChangeListener sminListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sminProgress = i;
                Log.i("Config","smin: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
    private SeekBar.OnSeekBarChangeListener vminListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                vminProgress = i;
                Log.i("Config","vmin: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
    private SeekBar.OnSeekBarChangeListener hmaxListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hmaxProgress = i;
                Log.i("Config","hmax: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
    private SeekBar.OnSeekBarChangeListener smaxListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                smaxProgress = i;
                Log.i("Config","smax: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
    private SeekBar.OnSeekBarChangeListener vmaxListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                vmaxProgress = i;
                Log.i("Config","vmax: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private SeekBar.OnSeekBarChangeListener minNListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minNeighborsProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();    //libera camera para outra activity

    }

    private RadioGroup.OnCheckedChangeListener cameraConfig(final CameraSettings cameraSet) {
        return new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (R.id.radioBorda == checkedId) {
                    layoutBorder.setVisibility(View.VISIBLE);
                    layoutSkin.setVisibility(View.GONE);
                    layoutFace.setVisibility(View.GONE);
                    MainActivity.cSettings.setCongigView("BORDER");
                }

                else if (R.id.radioPele == checkedId) {
                    layoutBorder.setVisibility(View.GONE);
                    layoutSkin.setVisibility(View.VISIBLE);
                    layoutFace.setVisibility(View.GONE);
                    MainActivity.cSettings.setCongigView("SKIN");
                }
                else if (R.id.radioFace == checkedId){
                    layoutBorder.setVisibility(View.GONE);
                    layoutSkin.setVisibility(View.GONE);
                    layoutFace.setVisibility(View.VISIBLE);
                    MainActivity.cSettings.setCongigView("FACE");
                }

            }
        };
    }
}

