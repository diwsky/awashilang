package com.diwangkoro.awashilang.Alerting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.diwangkoro.awashilang.R;

import java.io.IOException;
import static android.media.AudioManager.STREAM_MUSIC;


public class MainAlerting extends Activity {

    private MediaPlayer Alarm;
    private AudioManager am;
    private Camera camera;
    private boolean isFlashOn;
    private boolean isBlinkOn;
    private boolean hasFlash;
    private Camera.Parameters params;
    TextView text;
    private int oldStreamVolume, oldMode;
    private boolean isSpeakerPhoneOn, isVolumeMax;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.alerting);

        text = (TextView) findViewById(R.id.fullscreen_content);

        mPref = getSharedPreferences(getPackageName().toString(), Context.MODE_PRIVATE);

        //android.provider.Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,0);

        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();

//        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        oldStreamVolume = am.getStreamVolume(STREAM_MUSIC);
//        isVolumeMax = false;
//        isFlashOn = false;
//        isBlinkOn = false;
        displayMessage();
    }



    public void displayMessage(){
        text.setText(mPref.getString("ALERT_TEXT","AwasHilang!!!!"));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        setMaxVolume();
//
//        StartBlink();
//        turnOnAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setMaxVolume();
//
//        StartBlink();
//        turnOnAlarm();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        StartBlink();
//        setMaxVolume();
//        turnOnAlarm();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass method first

//        turnOffAlarm();
//        turnBackVolume();
//        StopBlink();


    }

    @Override
    public void onStop() {
        super.onStop();  // Always call the superclass method first

//        turnOffAlarm();
//        turnBackVolume();
//        StopBlink();
        finish();
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
            AlertDialog alert = new AlertDialog.Builder(MainAlerting.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
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
}
