package com.example.kollins.libras_android.network;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kollins on 25/08/17.
 */

public class DataOut implements Runnable {

    private final int MAX_SIZE = 100000;

    private Comunication outputConnection;
    private ReentrantLock imageLock;
    private ByteArrayOutputStream baos;
    private Bitmap.Config conf = Bitmap.Config.ARGB_4444;
    private Bitmap bmp;
    private byte[] bytes;

    public DataOut(Comunication comunication, ReentrantLock imageLock) {
        outputConnection = comunication;
        this.imageLock = imageLock;
        baos = new ByteArrayOutputStream();
    }

    @Override
    public void run() {
        while (true) {

            synchronized (imageLock){
                try {
                    imageLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            bmp = Bitmap.createBitmap(outputConnection.getOutMat().width(), outputConnection.getOutMat().height(), conf);
            Utils.matToBitmap(outputConnection.getOutMat(), bmp);
            baos.reset();

            bmp = Bitmap.createScaledBitmap(bmp, 700, 700, true);

            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bytes = baos.toByteArray();

            try {
                if(outputConnection.getOutputStream() != null) {
                    Log.i("Tamanho", "Bytes: " + bytes.length + "\n");
                    if(bytes.length > 0) {
                        outputConnection.getOutputStream().writeInt(bytes.length);
                        outputConnection.getOutputStream().write(bytes);
                        Thread.sleep(200);
                    }
                }
                else
                    break;
            } catch (IOException e) {
                e.printStackTrace();
                outputConnection.updateText("Falha na conexão\nToque para tentar novamente");
                outputConnection.disconnectImage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

