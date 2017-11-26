package com.example.kollins.libras_android.camera_management;

import android.util.Log;

import com.example.kollins.libras_android.CameraView;
import com.example.kollins.libras_android.MainActivity;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kollins on 17/08/17.
 */

public class CameraAction implements Runnable {

    private String command;
    private Mat src;
    private Mat out;

    private ReentrantLock lock;

    //private Mat auxOut;
    private Mat auxSkin;
    private Mat skinMask;
    private MatOfRect faces = new MatOfRect();
    private Mat auxOutBorder;
    private Mat auxFace;

    public CameraAction(ReentrantLock lock) {
        this.lock = lock;
        skinMask = new Mat();
        auxSkin = new Mat();
        faces = new MatOfRect();
        auxOutBorder = new Mat();
        auxFace = new Mat();
    }

    @Override
    public void run() {
        while (true) {

            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (getCommandAction().equals("BORDER")) {

                    //setOutMat(new Mat());
                    Mat auxOut = new Mat();
                    Imgproc.Canny(getSrcMat(), auxOut, CameraView.threshold1Progress, CameraView.threshold2Progress);
                    auxOut.copyTo(getOutMat());
                    auxOut.release();

                } else if (getCommandAction().equals("SKIN")) {

                    Mat auxOut = new Mat();
//                    Mat auxSkin = new Mat();
//                    Mat skinMask = new Mat();


                    Imgproc.cvtColor(getSrcMat(), auxSkin, Imgproc.COLOR_BGR2HSV, 3);

                    Scalar lower = new Scalar(CameraView.hminProgress, CameraView.sminProgress, CameraView.vminProgress);
                    Scalar upper = new Scalar(CameraView.hmaxProgress, CameraView.smaxProgress, CameraView.vmaxProgress);

                    //Log.i("HSV", "Hmin: " + CameraView.hminProgress + "\nSmin: " + CameraView.sminProgress + "\nVmin" + CameraView.vminProgress + "\n\nHmax: " + CameraView.hmaxProgress + "\nSmax: " + CameraView.smaxProgress + "\nVmax: " + CameraView.vmaxProgress);

                    Core.inRange(auxSkin, lower, upper, skinMask);

                    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(11, 11));
                    Imgproc.erode(skinMask, skinMask, kernel);
                    Imgproc.dilate(skinMask, skinMask, kernel);

                    Imgproc.GaussianBlur(skinMask, skinMask, new Size(3, 3), 0);

                    Core.bitwise_and(getSrcMat(), getSrcMat(), auxOut, skinMask);

                    auxOut.copyTo(getOutMat());

//                    auxOut.release();
//                    auxSkin.release();
//                    skinMask.release();

                } else if (getCommandAction().equals("FACE")) {

                    Mat auxOut = new Mat();
//                    MatOfRect faces = new MatOfRect();

                    Imgproc.cvtColor(getSrcMat(), auxOut, Imgproc.COLOR_BGR2GRAY, 1);

                    // Use the classifier to detect faces
                    if (MainActivity.cascadeClassifier != null) {
                        MainActivity.cascadeClassifier.detectMultiScale(auxOut, faces, 1.1, CameraView.minNeighborsProgress, 2,
                                new Size(CameraSettings.absoluteFaceSize, CameraSettings.absoluteFaceSize), new Size());
                    }

                    // If there are any faces found, draw a rectangle around it
                    Rect[] facesArray = faces.toArray();
                    for (int i = 0; i <facesArray.length; i++) {
                        //Imgproc.rectangle(getSrcMat(), facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
                        Point tr = new Point(facesArray[i].tl().x + (double)facesArray[i].width,facesArray[i].tl().y);
                        Point bl = new Point(facesArray[i].tl().x,facesArray[i].tl().y + (double)facesArray[i].height);
                        Imgproc.fillConvexPoly(getSrcMat(),new MatOfPoint(facesArray[i].tl(), tr, facesArray[i].br(),bl),new Scalar(0,0,0,0));

                    }

                    getSrcMat().copyTo(getOutMat());

                    auxOut.release();
//                    faces.release();


                }else if (getCommandAction().equals("ALL")) {

                    Mat auxOut = new Mat();
                    Mat auxOutFace = new Mat(getSrcMat().size(), getSrcMat().type(),new Scalar(255,255,255,255));

                    setOutMat(new Mat());

                    /**********BORDER****************/
                    Imgproc.Canny(getSrcMat(), auxOutBorder, CameraView.threshold1Progress, CameraView.threshold2Progress);

                    /**************SKIN**************/
                    Imgproc.cvtColor(getSrcMat(), auxSkin, Imgproc.COLOR_BGR2HSV, 3);

                    Scalar lower = new Scalar(CameraView.hminProgress, CameraView.sminProgress, CameraView.vminProgress);
                    Scalar upper = new Scalar(CameraView.hmaxProgress, CameraView.smaxProgress, CameraView.vmaxProgress);

                    //Log.i("HSV", "Hmin: " + CameraView.hminProgress + "\nSmin: " + CameraView.sminProgress + "\nVmin" + CameraView.vminProgress + "\n\nHmax: " + CameraView.hmaxProgress + "\nSmax: " + CameraView.smaxProgress + "\nVmax: " + CameraView.vmaxProgress);

                    Core.inRange(auxSkin, lower, upper, skinMask);

                    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(11, 11));
                    Imgproc.erode(skinMask, skinMask, kernel);
                    Imgproc.dilate(skinMask, skinMask, kernel);

                    Imgproc.GaussianBlur(skinMask, skinMask, new Size(3, 3), 0);

                    /**************FACE**********************/
                    Imgproc.cvtColor(getSrcMat(), auxFace, Imgproc.COLOR_BGR2GRAY, 1);

                    // Use the classifier to detect faces
                    if (MainActivity.cascadeClassifier != null) {
                        MainActivity.cascadeClassifier.detectMultiScale(auxFace, faces, 1.1, CameraView.minNeighborsProgress, 2,
                                new Size(CameraSettings.absoluteFaceSize, CameraSettings.absoluteFaceSize), new Size());
                    }

                    // If there are any faces found, draw a rectangle around it
                    Rect[] facesArray = faces.toArray();
                    for (int i = 0; i <facesArray.length; i++) {
                        //Imgproc.rectangle(getSrcMat(), facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
                        Point tr = new Point(facesArray[i].tl().x + (double)facesArray[i].width,facesArray[i].tl().y);
                        Point bl = new Point(facesArray[i].tl().x,facesArray[i].tl().y + (double)facesArray[i].height);
                        Imgproc.fillConvexPoly(auxOutFace,new MatOfPoint(facesArray[i].tl(), tr, facesArray[i].br(),bl),new Scalar(0,0,0,0));

                    }

                    Imgproc.cvtColor(auxOutFace, auxOutFace, Imgproc.COLOR_BGR2GRAY, 1);

                    /**************JOIN OUTPUT**************/
                    Core.bitwise_and(auxOutBorder, auxOutFace, auxOut);
                    auxOut.copyTo(getOutMat(),skinMask);

                    auxOut.release();
                    auxOutFace.release();

                }else if (getCommandAction().equals("NONE")) {
                    setOutMat(getSrcMat());
                }

                lock.notifyAll();
            }
        }

    }

    synchronized public void setCommandAction(String command) {
        this.command = command;
    }

    synchronized public String getCommandAction() {
        return this.command;
    }

    synchronized public void setSrcMat(Mat src) {
        this.src = src;
    }

    synchronized public void setOutMat(Mat out) {
        this.out = out;
    }

    synchronized public Mat getSrcMat() {
        return this.src;
    }

    synchronized public Mat getOutMat() {
        return this.out;
    }
}
