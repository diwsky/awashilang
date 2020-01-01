package com.diwangkoro.awashilang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/**
 * Created by user on 4/23/2016.
 */
public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, AutoStartIntentService.class);
        serviceIntent.putExtra("caller", "RebootReceiver");
        context.startService(serviceIntent);

        WakeLocker.acquire(context);
        WakeLocker.release();
    }
}
