package com.diwangkoro.awashilang.Alerting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.diwangkoro.awashilang.WakeLocker;

/**
 * Created by user on 2/23/2016.
 */
public class M_RecAlarmCharger extends BroadcastReceiver {

    //Variabel
    boolean ACTIVE_ALARM_CHARGE;
    SharedPreferences mPref;
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.ctx = context;
        String action = intent.getAction();
        mPref = context.getApplicationContext().getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        ACTIVE_ALARM_CHARGE = mPref.getBoolean("ACTIVE_ALARM_CHARGER", true);

        if(action.equals(Intent.ACTION_POWER_CONNECTED)){
            // Do something when power connected
            Toast.makeText(context.getApplicationContext(), "Charger Connected", Toast.LENGTH_SHORT).show();
            if (!ACTIVE_ALARM_CHARGE) {
                Intent i1 = new Intent(context, AlarmChargerDialog.class);
                i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i1);
            }
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            // Do something when power disconnected
            Toast.makeText(context, "Charger Disconnected", Toast.LENGTH_SHORT).show();
            if (ACTIVE_ALARM_CHARGE) {
                Intent i2 = new Intent(context, Service_Alarm.class);
                context.startService(i2);
            }
        }

        //WakeLock receiver
        WakeLocker.acquire(context.getApplicationContext());
        WakeLocker.release();
    }
}
