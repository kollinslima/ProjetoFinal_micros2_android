package com.example.kollins.libras_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kollins.libras_android.camera_management.CameraSettings;
import com.example.kollins.libras_android.network.Comunication;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "OPENCV";
    private CameraBridgeViewBase mOpenCvCamera;
    private ToggleButton cameraEnable;
    private Button cameraView;

    public static TextView textoRetorno;

    public static CascadeClassifier cascadeClassifier;
    public static CameraSettings cSettings;

    private Comunication socketImage;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                    initializeOpenCVDependencies();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private void initializeOpenCVDependencies() {

        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);


            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            // Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        socketImage = new Comunication();
        socketImage.connectImage();

        cSettings = new CameraSettings("ALL",socketImage);

        mOpenCvCamera = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCamera.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCamera.setCvCameraViewListener(cSettings);
        mOpenCvCamera.disableView();

        cameraEnable = (ToggleButton) findViewById(R.id.camera_on_off);
        cameraEnable.setOnCheckedChangeListener(switchCameraState());


        mOpenCvCamera.setVisibility(SurfaceView.VISIBLE);
        cameraView = (Button) findViewById(R.id.camera_view);
        cameraView.setOnClickListener(callCameraView());

        textoRetorno = (TextView) findViewById(R.id.letraRetorno);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(this);

        //actionBar.setDisplayHomeAsUpEnabled(true);

        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
    }

    private View.OnClickListener callCameraView() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CameraView.class);
                Bundle params = new Bundle();
                intent.putExtras(params);
                mOpenCvCamera.disableView();    //libera camera para outra activity
                cameraEnable.setChecked(false);
                startActivity(intent);

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener switchCameraState() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //mOpenCvCamera.setVisibility(SurfaceView.VISIBLE);
                    cSettings.setCongigView("ALL");
                    mOpenCvCamera.enableView();
                    
                }
                else{
                    mOpenCvCamera.disableView();
                    //mOpenCvCamera.setVisibility(SurfaceView.INVISIBLE);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraEnable.setChecked(false);
        mOpenCvCamera.disableView();
        CameraSettings.setConfigurationMode(Boolean.FALSE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraSettings.setConfigurationMode(Boolean.TRUE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketImage.disconnectImage();
    }

    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                socketImage.connectImage();
                Log.i("Socket","Toque na tela");
                break;

            case MotionEvent.ACTION_MOVE:
                //User is moving around on the screen
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        int id = item.getItemId();
//
//        switch (id){
//            case R.id.cameraView:
//                callCameraView();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private void toast (String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private Context getContext(){
        return this;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
