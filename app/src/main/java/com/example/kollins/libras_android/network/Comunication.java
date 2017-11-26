package com.example.kollins.libras_android.network;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kollins.libras_android.MainActivity;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kollins on 23/08/17.
 */

public class Comunication extends AppCompatActivity {

    private final String HOST = "143.107.235.43";
    private final int PORT = 5000;

    private DataOutputStream outputStream;
    private Scanner inputStream;

    private Thread inputThread;
    private Thread outputThread;

    private Mat outMat;

    private ReentrantLock imageLock;

    private Socket socket = null;

    public void connectImage(){

        final Comunication comunication = this;

        if(socket == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        socket = new Socket(HOST, PORT);
                        outputStream = new DataOutputStream(socket.getOutputStream());
                        inputStream = new Scanner(socket.getInputStream());

                        imageLock = new ReentrantLock();

                        inputThread = new Thread(new DataIn(comunication));
                        inputThread.start();
                        outputThread = new Thread(new DataOut(comunication,imageLock));
                        outputThread.start();

                        Log.i("Socket", "Conexão realizada");

                        updateText("Aguardando\nCaractere...");

                    } catch (IOException e) {
                        e.printStackTrace();
                        updateText("Falha na conexão\nToque para tentar novamente");

                    }


                }
            }).start();
        }

    }

    public void updateText(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.textoRetorno.setText(s);
            }
        });
    }

    public void disconnectImage() {

        try {
            socket.close();
            outputStream.close();
            inputStream.close();
            socket = null;
            outputStream = null;
            inputStream = null;
            inputThread = null;
            outputThread = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("Socket", "Conexão encerrada");
    }

    synchronized public void writeImage(Mat outMat) {
        this.outMat = outMat;
        synchronized (imageLock){
            imageLock.notifyAll();
        }
    }

    synchronized public Mat getOutMat(){
        return outMat;
    }

    public DataOutputStream getOutputStream() {
        if(outputStream != null)
            return outputStream;
        else
            return null;
    }

    public Scanner getInputStream() {
        if(inputStream != null)
            return inputStream;
        else
            return null;
    }

    public Socket getSocket() {
        return socket;
    }

}

