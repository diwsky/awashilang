package com.diwangkoro.awashilang.gcm;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.diwangkoro.awashilang.Mail;
import com.diwangkoro.awashilang.Monitoring.M_capture;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 5/3/2016.
 */
public class Command_gcm {

    boolean ICON_STATUS;
    SharedPreferences mPref;
    public static DevicePolicyManager devicePolicyManager;
    static final int RESULT_ENABLE = 1;
    public static ComponentName compName;

    private Camera mCamera;
    private Camera.Parameters parameters;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
    private String curDate = sdf.format(new Date());
    private String whitebalance;
    private final String APP_SAVING_PATH = "/Awas Hilang!/";
    private Bitmap bmp;
    String imagepath;

    public StringBuffer mBatteryLevel (Context context) {

        StringBuffer sb= new StringBuffer();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.getApplicationContext().registerReceiver(null,ifilter);
        int level2 = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        String level = String.valueOf(level2);

        sb.append("\nBattery Level:--- "+level+"%");

        return sb;
    }

    public String getLogs (Context context) {


        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int counter =0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy_HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM_HH:mm");


        cursor.moveToLast();
        List<LOGSin> listLogs = new ArrayList<>();
        do {
            String numId = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
            String raw_date = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
            Date callDate = new Date(Long.valueOf(raw_date));
            String date = sdf.format(callDate);
            String date2 = sdf2.format(callDate);

            switch (Integer.parseInt(type)) {
                case CallLog.Calls.OUTGOING_TYPE:
                    type = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    type = "INCOMING";

                    break;

                case CallLog.Calls.MISSED_TYPE:
                    type = "MISSED";

                    break;
            }

            LOGSin newLogs = new LOGSin();
            newLogs.nomor = numId;
            newLogs.tipe = type;
            newLogs.date = String.valueOf(callDate);
            newLogs.time = duration;
            listLogs.add(newLogs);
            counter += 1;
        } while (cursor.moveToPrevious() && counter != 10);


        cursor.close();
        Gson gson = new Gson();
        String LOGSin = gson.toJson(listLogs);
        return LOGSin;

    }

    private class LOGSin {
        String nomor;
        String tipe;
        String date;
        String time;
    }

    public void hideLauncherIcon(Context context){

        mPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        mPref.edit().putBoolean("ICON_STATUS", false).apply();
        Log.d("error", mPref.getString("LAUNCHER_NUMBER", "12345"));
        Log.e("HIDE", "OK");

    }

    public void unhideLauncherIcon(Context context) {
        mPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        mPref.edit().putBoolean("ICON_STATUS", true).apply();
        Log.e("UNHIDE", "OK");
    }

    public void locknow(Context context) {

        compName = new ComponentName(context, com.diwangkoro.awashilang.Locking.myAdmin.class);
        devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.lockNow();
    }

    public void AddDeviceAdmin(Context context) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should allow this application to use Locking feature");
        context.startActivity(intent);

    }

    public String getNumber(Context context) {

        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String number = tMgr.getLine1Number();

        if (number.equalsIgnoreCase("")) {
            number = "Nomor kartu anda belum terdaftar pada smartphone.";
        }
        return number;
    }

    public String getSMS(Context context, String folder) {

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"+folder), null, null, null, null);
        int counter = 0;

        cursor.moveToFirst(); //Start cursor from the last received SMS
        List<SMSout> listSms = new ArrayList<>();

        do {
            // String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            Date callDayTime = new Date(Long.valueOf(date));

            SMSout newSMS = new SMSout();
            newSMS.number = address;
            newSMS.message = body;
            newSMS.date = String.valueOf(callDayTime);
            listSms.add(newSMS);
            counter += 1;


        } while (cursor.moveToNext() && counter != 5);
        cursor.close();

        Gson gson = new Gson();
        String SMSout = gson.toJson(listSms);

        return SMSout;

        //String test = String.valueOf(sb);
        //Log.d("response save sms inbox", test);
    }

    private class SMSout {
        String number;
        String message;
        String date;
    }


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
        //Toast.makeText(ctx, "Starting snapshot",Toast.LENGTH_SHORT).show();
        // here below "this" is activity context.

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


    public void sendEmail(Context context, String subject, String body, String mode) {
        M_capture capture = new M_capture();

        //---------Sending image to email------//
        Mail m = new Mail("awashilang.apps@gmail.com", "diditganteng");
        String[] toArr = {mPref.getString("EMAIL","diwangkoro270@gmail.com")};
        m.setTo(toArr);
        m.setFrom("awashilang.apps@gmail.com");
        m.setSubject(subject);
        m.setBody(body);

        try  {

            if (mode=="photo") {
                m.addAttachment("/sdcard/Awas Hilang!/"+capture.getImageDate()+".png");
            }
            if (m.send()) {
                //Toast.makeText(context.getApplicationContext(), "Email was sent", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(context.getApplicationContext(), "Email was not sent", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ReceiverSMS", "Could not send email", e);
        }
    }

}
