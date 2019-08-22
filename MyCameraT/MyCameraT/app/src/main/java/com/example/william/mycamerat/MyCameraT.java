package com.example.william.mycamerat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.app.FragmentTransaction;


import com.example.william.mycamerat.bluetoothchat.BluetoothChatFragment;
import com.example.william.mycamerat.bluetoothchat.Constants;
import com.example.william.mycamerat.common.activities.SampleActivityBase;
import com.example.william.mycamerat.common.logger.LogWrapper;
import com.example.william.mycamerat.common.logger.MessageOnlyLogFilter;

public class MyCameraT extends SampleActivityBase
{

    private Camera mCamera;
    private CameraPreview mPreview;
    public static final String TAG = "MyCameraT";
    private Camera.Parameters mParameters;
    private Context mContext = this;
    private MyOrientationDetector cameraOrientation;
    private BluetoothChatFragment fragment;


    int mNumberOfCameras;
    int mCameraCurrentlyLocked;

    // The first rear facing camera
    int mDefaultCameraId;

    int mScreenWidth, mScreenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //create bluetooth fragment
        if (savedInstanceState == null) {
            //bluetooth
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothChatFragment();
            transaction.add(fragment,"BluetoothChatFragment");
            transaction.commit();
        }
//        else{
//            Log.d(TAG,"onCreate saved");
//            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//            fragment = new BluetoothChatFragment();
//            getSupportFragmentManager().beginTransaction().add(fragment,"BluetoothChatFragment").commit();
//        }

        // no title window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get screen size
        WindowManager wManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wManager.getDefaultDisplay();
        mScreenHeight = display.getHeight();
        mScreenWidth = display.getWidth();

        // get default cameraID
        mDefaultCameraId = getDefaultCameraId();
        mCameraCurrentlyLocked = mDefaultCameraId;
        //
        cameraOrientation = new MyOrientationDetector(mContext);
        if (cameraOrientation.canDetectOrientation()) {
            cameraOrientation.enable();
        } else {
            Log.e(TAG,"can't Detect Orientation!");
        }

    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
        //....
//        if(fragment == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            fragment = new BluetoothChatFragment();
//            transaction.add(fragment,"BluetoothChatFragment");
//            transaction.commit();
//        }

        // set content
        setContentView(R.layout.activity_hello_custom_camera);

        // use button listener
        ImageButton captureButton = (ImageButton) findViewById(R.id.image_button_capture);
        captureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // get an image from the camera
                showPokerOrPic();
            }
        });

        //use button listener to scan poker
//        ImageButton scanButton = (ImageButton) findViewById(R.id.image_button_scan);
//        scanButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                testPokerShowing();
//            }
//        });

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        // put preview to content
        preview.addView(mPreview, 0);

        // Open the default i.e. the first rear facing camera.
        mCamera = getCameraInstance(mCameraCurrentlyLocked);
        //set auto focus
        mParameters = mCamera.getParameters();
        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(mParameters);

        mPreview.setCamera(mCamera);
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause");
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null)
        {
            mPreview.setCamera(null);
            Log.d(TAG, "onPause --> Realease camera");
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mPreview.getHolder().getSurface().release();
            mPreview = null;
        }

    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy");
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        fragment = null;
        super.onDestroy();
    }

    /**
     * get default Id
     *
     * @return
     */

    private void takePhoto() {
        if (mCamera != null) {
            Camera.Parameters cameraParameter = mCamera.getParameters();
            int orientation = cameraOrientation.getOrientation();
            cameraParameter.setRotation(90);
            cameraParameter.set("rotation", 90);
            if ((orientation >= 45) && (orientation < 135)) {
                cameraParameter.setRotation(180);
                cameraParameter.set("rotation", 180);
            }
            if ((orientation >= 135) && (orientation < 225)) {
                cameraParameter.setRotation(270);
                cameraParameter.set("rotation", 270);
            }
            if ((orientation >= 225) && (orientation < 315)) {
                cameraParameter.setRotation(0);
                cameraParameter.set("rotation", 0);
            }
//              WindowManager wManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//              int rotation = wManager.getDefaultDisplay().getRotation();
//              int angle = 0;
//              switch (rotation) {
//                  case Surface.ROTATION_0:
//                      angle = 90;
//                      break;
//                  case Surface.ROTATION_90:
//                      angle = 180;
//                      break;
//                  case Surface.ROTATION_180:
//                      angle = 270;
//                      break;
//                  case Surface.ROTATION_270:
//                      angle = 0;
//                      break;
//              }
//            Log.d(TAG,"rotation: "+Integer.toString(angle));
//            cameraParameter.setRotation(angle);
//            cameraParameter.set("rotation", angle);

            mCamera.setParameters(cameraParameter);
            mCamera.takePicture(null, null, mPicture);
        }
    }

    private int getDefaultCameraId()
    {
        Log.d(TAG, "getDefaultCameraId");
        int defaultId = -1;

        // Find the total number of cameras available
        mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++)
        {
            Camera.getCameraInfo(i, cameraInfo);
            Log.d(TAG, "camera info: " + cameraInfo.orientation);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
            {
                defaultId = i;
            }
        }
        if (-1 == defaultId)
        {
            if (mNumberOfCameras > 0)
            {
                // no back camera
                defaultId = 0;
            }
            else
            {
                // no camera
                Toast.makeText(getApplicationContext(), "no camera",
                        Toast.LENGTH_LONG).show();
            }
        }
        return defaultId;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int cameraId)
    {
        Log.d(TAG, "getCameraInstance");
        Camera c = null;
        try
        {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        }
        catch (Exception e)
        {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
            Log.e(TAG, "Camera is not available");
        }
        return c; // returns null if camera is unavailable
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type)
    {
        Log.d(TAG, "getOutputMediaFile");
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = null;
        try
        {
            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MyCameraApp");

            Log.d(TAG,
                    "Successfully created mediaStorageDir: " + mediaStorageDir);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "Error in Creating mediaStorageDir: "
                    + mediaStorageDir);
        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                // need authority
                // <uses-permission
                // android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                Log.d(TAG,
                        "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
        else if (type == MEDIA_TYPE_VIDEO)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        }
        else
        {
            return null;
        }

        return mediaFile;
    }

    private PictureCallback mPicture = new PictureCallback()
    {

        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            Log.d(TAG, "onPictureTaken");

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null)
            {
                Log.d(TAG,
                        "Error creating media file, check storage permissions: ");
                return;
            }

            try
            {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            }
            catch (FileNotFoundException e)
            {
                Log.d(TAG, "File not found: " + e.getMessage());
            }
            catch (IOException e)
            {
                Log.d(TAG,
                        "Error accessing file: " + e.getMessage());
            }

            // restart preview
            mCamera.stopPreview();
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile))
            try {
                showPic(pictureFile);
            }
            catch (Exception e){
                Log.e(TAG,"can't draw pic");
            }
            mCamera.startPreview();
        }

    };
    private void showPic(File pictureFile){
        Log.d(TAG,"showing photo");
        Intent intent = new Intent(this, ImagePagerActivity.class);
        intent.putExtra("images",getPhotoUrls(pictureFile.toString()));
        startActivity(intent);
    }
    private void showPokerOrPic(){
        BluetoothChatFragment btFragment = fragment;
        Log.d(TAG,"showing poker number " + Integer.toString(btFragment.POKER_NUMBER));
        if(btFragment.POKER_NUMBER != Constants.NO_POKER_MATCHED) { //if so, take a photo
            Intent intent = new Intent(this, PokerShowActivity.class);
            if(btFragment.POKER_NUMBER == Constants.EASTER_EGG)
                intent.putExtra("pokerName","easter_egg");
            else if(btFragment.POKER_NUMBER == Constants.GROUP_MEMBERS)
                intent.putExtra("pokerName","group_members");
            else
                intent.putExtra("pokerName","poker"+Integer.toString(btFragment.POKER_NUMBER));
            btFragment.POKER_NUMBER = Constants.NO_POKER_MATCHED;
            startActivity(intent);
        }else{
            takePhoto();
        }
    }

    /** Create a chain of targets that will receive log data */
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        com.example.william.mycamerat.common.logger.Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);


        com.example.william.mycamerat.common.logger.Log.i(TAG, "Ready");
    }

    public static String []getPokerUrls(int poker) {
        return new String[]{"drawable://"+poker};
    }
    public static String[] getPhotoUrls(String picAddress){
        return new String[]{"file:///"+picAddress};
    }

}