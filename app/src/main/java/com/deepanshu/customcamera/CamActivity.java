package com.deepanshu.customcamera;

import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamActivity extends AppCompatActivity implements View.OnClickListener {
    private Camera mCamera;

    public static final String TAG = "CamActivity";
    private boolean isFilterOpen=false;
    private ImageView filterButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        CameraPreview mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        if (preview != null) {
            preview.addView(mPreview);
        }
        // Add a listener to the Capture button
        ImageView captureButton = (ImageView) findViewById(R.id.button_capture);
        if (captureButton != null) {
            captureButton.setOnClickListener(this);
        }
        // Add listener to filter button
        filterButton = (ImageView) findViewById(R.id.filterButton);
        if (filterButton != null) {
            filterButton.setOnClickListener(this);
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    // Method for set the camera filters
    public void colorEffectFilter(View v){
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            switch (v.getId()) {
                case R.id.effectNone:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectAqua:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectBlackboard:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_BLACKBOARD);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectMono:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectNegative:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectPosterize:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectSepia:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectSolarize:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectWhiteboard:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_WHITEBOARD);
                    mCamera.setParameters(parameters);
                    break;
            }
        }catch (Exception ex){
            Log.d(TAG,ex.getMessage());
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            MediaScannerConnection.scanFile(CamActivity.this,
                    new String[] { pictureFile.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /** File for saving an image */
    private static File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CamPictures");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(mediaStorageDir.getAbsolutePath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.filterButton:
                if (!isFilterOpen){
                    findViewById(R.id.record_filter_layout).setVisibility(View.VISIBLE);
                    filterButton.setImageResource(R.drawable.filter_on);
                    isFilterOpen=true;
                }
                else {
                    findViewById(R.id.record_filter_layout).setVisibility(View.GONE);
                    filterButton.setImageResource(R.drawable.filter_off);
                    isFilterOpen=false;
                }
                break;
            case R.id.button_capture:
                mCamera.takePicture(null,null,mPicture);
                break;

        }
    }
}
