package com.diwangkoro.awashilang.Alerting;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.diwangkoro.awashilang.R;

import java.io.IOException;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by user on 4/27/2016.
 */
public class Service_Alarm extends Service {

    private MediaPlayer Alarm;
    private AudioManager am;
    private Camera camera;
    private boolean isFlashOn;
    private boolean isBlinkOn;
    private boolean hasFlash;
    private Camera.Parameters params;
    private String text;
    private int oldStreamVolume, oldMode;
    private boolean isSpeakerPhoneOn, isVolumeMax;
    SharedPreferences mPref;
    private NotificationManager mNM;

    private int NOTIFICATION = 147;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        Service_Alarm getService() {
            return Service_Alarm.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent ahay = new Intent(getApplicationContext(), MainAlerting.class);
        ahay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ahay.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        ahay.addFlags(Intent.FLAG_FROM_BACKGROUND);
        getApplicationContext().startActivity(ahay);

        mPref = getSharedPreferences(getPackageName().toString(), Context.MODE_PRIVATE);

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        text = mPref.getString("ALERT_TEXT","Klik disini untuk menghentikan alarm");

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification(text);

        android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT,0);

        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        oldStreamVolume = am.getStreamVolume(STREAM_MUSIC);
        isVolumeMax = false;
        isFlashOn = false;
        isBlinkOn = false;
        //displayMessage();

        setMaxVolume();
        StartBlink();
        turnOnAlarm();

        SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(this,new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver );

    }

    public class SettingsContentObserver extends ContentObserver {
        int previousVolume;
        Context context;

        public SettingsContentObserver(Context c, Handler handler) {
            super(handler);
            context=c;

            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            if (currentVolume<previousVolume) {
                currentVolume=previousVolume;
            }

//            int delta=previousVolume-currentVolume;

//            if(delta>0)
//            {
//                previousVolume=currentVolume;
//            }
//            else if(delta<0)
//            {
//                previousVolume=currentVolume;
//            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(stopServiceReceiver);

        SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(this,new Handler());
        getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);

        mNM.cancel(NOTIFICATION);

        turnOffAlarm();
        turnBackVolume();
        StopBlink();
    }

    private void setMaxVolume(){

        if (!isVolumeMax){
            am.setStreamVolume(STREAM_MUSIC, am.getStreamMaxVolume(STREAM_MUSIC), 0);
            isVolumeMax = true;}

    }

    private void turnBackVolume(){
        if(isVolumeMax){
            am.setStreamVolume(STREAM_MUSIC, oldStreamVolume, 0);
            isVolumeMax = false;}
    }

    private void turnOnAlarm(){
        if (Alarm==null){
            Alarm = MediaPlayer.create(this, R.raw.tes);
            Alarm.setVolume(1.0f, 1.0f);
            Alarm.setLooping(true);}
        Alarm.start();
    }


    private void turnOffAlarm(){
        if (Alarm!=null){
            Alarm.release();
            Alarm=null;}
    }
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
// changing button/switch image
        }
    }

    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            // changing button/switch image

        }
    }

    private void toggleFlash(){
        if (!isFlashOn){
            turnOnFlash();
        }
        else{
            turnOffFlash();
        }
    }

    private void FlashCheck(){
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(getApplicationContext())
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.show();
            return;
        }
    }

    private void StartBlink(){
        if (!isBlinkOn) {
            FlashCheck();
            Thread t = new Thread() {
                public void run() {
                    try {
                        // Switch on the cam for app's life

                        // Turn on Cam
                        camera = Camera.open();
                        params = camera.getParameters();
                        try {
                            camera.setPreviewDisplay(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        camera.startPreview();

                        for (int i = 0; i < 500 * 2; i++) {
                            toggleFlash();
                            sleep(100);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            t.start();
            isBlinkOn = true;
        }
    }

    private void StopBlink(){
        if (isBlinkOn){
            if (camera!=null) {
                //camera.open();
                camera.stopPreview();
                camera.release();
                camera = null;
                isBlinkOn = false;
                Log.d("KAMERA NULL","asd");
            }
        }
    }

    private void showNotification(String text) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        //----------Default------------
        //CharSequence text = "Klik disini untuk menghentikan alarm";

        // Menggunakan broadcastreceiver untuk menghentikan service alarm (stopSelf)
        registerReceiver(stopServiceReceiver, new IntentFilter("myFilter"));

        // PendingIntent untuk melakukan aksi saat notifikasi ditekan
        PendingIntent stopServiceIntent = PendingIntent.getBroadcast(getApplicationContext(),0,
                new Intent("myFilter"),PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.stopalarm)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("AwasHilang!")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(stopServiceIntent)  // The intent to send when the entry is clicked
                .build();
        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };

}
