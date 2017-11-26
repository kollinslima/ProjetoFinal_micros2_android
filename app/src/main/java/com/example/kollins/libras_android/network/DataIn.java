package com.example.kollins.libras_android.network;

import com.example.kollins.libras_android.MainActivity;

import org.opencv.core.Scalar;

import java.io.InputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by kollins on 25/08/17.
 */

public class DataIn implements Runnable {

    private Comunication inputConnection;

    public DataIn(Comunication comunication) {
        inputConnection = comunication;
    }

    @Override
    public void run() {
        while (true){
            try {
                if(inputConnection.getInputStream() != null)
                    inputConnection.updateText(inputConnection.getInputStream().nextLine());
                else
                    break;
            }catch (NoSuchElementException e){
                break;
            }
        }
    }
}
