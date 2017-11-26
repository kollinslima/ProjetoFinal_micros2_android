package com.example.kollins.libras_android.camera_management;

import android.util.Log;

import com.example.kollins.libras_android.network.Comunication;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kollins on 15/08/17.
 */

public class CameraSettings implements CameraBridgeViewBase.CvCameraViewListener2{

    private String command;
    private CameraAction cAction;
    private ReentrantLock lock;
    private Thread threadAction;

    private static Boolean configurationMode;

    private Comunication socketImage;

    public static int absoluteFaceSize;

    public CameraSettings(String command, Comunication socketImage) {
        lock = new ReentrantLock();
        cAction = new CameraAction(lock);
        cAction.setCommandAction(command);
        threadAction = new Thread(cAction);
        this.socketImage = socketImage;
        threadAction.start();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        absoluteFaceSize = (int) (height * 0.2);

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        cAction.setSrcMat(inputFrame.rgba());

        synchronized (lock){
            lock.notifyAll();
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(!configurationMode)
            socketImage.writeImage(cAction.getOutMat());

        return cAction.getOutMat();
    }

    public void setCongigView(String congigView) {
        cAction.setCommandAction(congigView);
    }

    public static void setConfigurationMode(Boolean state){
        configurationMode = state;
    }
}
