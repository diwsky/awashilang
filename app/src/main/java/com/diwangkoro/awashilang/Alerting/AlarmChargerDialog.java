package com.diwangkoro.awashilang.Alerting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import com.diwangkoro.awashilang.R;
import com.diwangkoro.awashilang.Settings;

/**
 * Created by user on 4/18/2016.
 */
public class AlarmChargerDialog extends Activity {

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.m_dummyview);

        mPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda ingin mengaktifkan AlarmCharger?");

        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                mPref.edit().putBoolean("ACTIVE_ALARM_CHARGER",true).apply();
                Toast.makeText(getApplicationContext(),"Anda telah mengaktifkan fitur AlarmCharger",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putBoolean("ACTIVE_ALARM_CHARGER",false).apply();
                Toast.makeText(getApplicationContext(),"Anda tidak mengaktifkan fitur AlarmCharger",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
