package com.diwangkoro.awashilang;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.diwangkoro.awashilang.Hidden.ReceiverHidden;


/**
 * Created by user on 2/3/2016.
 */
public class Splash extends Activity  {

    public ReceiverHidden RHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Initiate hidden receiver
        RHidden = new ReceiverHidden();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        this.registerReceiver(RHidden,intentFilter);

        setContentView(R.layout.splash);
        Thread logoTimer = new Thread(){
            public void run(){
                try {
                    sleep(3000);
                    Intent MainIntent = new Intent(Splash.this, LoginPage.class);
                    startActivity(MainIntent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        }; //end of thread
        logoTimer.start();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(RHidden,new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(RHidden);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
