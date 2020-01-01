package com.diwangkoro.awashilang.Monitoring;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.sun.mail.imap.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 3/28/2016.
 */
public class M_capture {

    private Camera mCamera;
    private Camera.Parameters parameters;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
    private String curDate = sdf.format(new Date());
    private String whitebalance;
    private final String APP_SAVING_PATH = "/Awas Hilang!/";
    private Bitmap bmp;
    String imagepath;

    SharedPreferences mPref;

    public Camera openFrontFacingCamera() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            android.hardware.Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("This is error", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
    }

    public void takeSnapShots(final Context ctx)
    {
        mPref = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        mPref.getString("DATE","");
        Toast.makeText(ctx, "Starting snapshot",Toast.LENGTH_SHORT).show();

        mCamera = openFrontFacingCamera();
        try {
            mCamera.setPreviewTexture(new SurfaceTexture(100));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            mCamera.release();
            e.printStackTrace();
            Log.e("error on this", "errorr errorr");
        }

        /** picture call back */
        Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera)
            {
                //-------------OLDSCHOOL-----------//
                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_SAVING_PATH;
                FileOutputStream outStream = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferQualityOverSpeed = true;
                options.inDither = true;
                options.inScaled = false;
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);

                //---------Saving image to Device------//
                try {
                    File dir = new File(fullPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    OutputStream fOut = null;
                    File file = new File(fullPath, curDate+".png");

                    file.createNewFile();
                    fOut = new FileOutputStream(file);

                    // 100 means no compression, the lower you go, the stronger the compression
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                    MediaStore.Images.Media.insertImage(ctx.getContentResolver(), file.getAbsolutePath(), file.getName(), "Picture from front camera");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    camera.stopPreview();
                    camera.release();
                    Toast.makeText(ctx, "Image snapshot Done",Toast.LENGTH_LONG).show();
                }

            }
        };
        whitebalance = Camera.Parameters.WHITE_BALANCE_AUTO;

        parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        parameters.setJpegThumbnailQuality(100);
        parameters.setWhiteBalance(whitebalance);
        parameters.setPictureSize(800,600);

        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mCamera.takePicture(null,null,jpegCallback);


    }

    public String getImageDate() {
        return curDate;
    }
}
