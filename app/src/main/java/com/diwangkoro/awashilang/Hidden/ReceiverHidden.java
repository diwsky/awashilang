package com.diwangkoro.awashilang.Hidden;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.diwangkoro.awashilang.Splash;
import com.diwangkoro.awashilang.WakeLocker;


/**
 * Created by user on 2/7/2016.
 */
public class ReceiverHidden extends BroadcastReceiver {

    String LAUNCHER_NUMBER = "";
    boolean ICON_STATUS;
    SharedPreferences mPref;
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {

        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        this.ctx = context;

        //Variabel akses kode masuk
        mPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        LAUNCHER_NUMBER = mPref.getString("LAUNCHER_NUMBER", "12345");
        ICON_STATUS = mPref.getBoolean("ICON_STATUS", true);

        if (!ICON_STATUS) {
            if (phoneNumber.equals(LAUNCHER_NUMBER)) {
                setResultData(null);
                if (!isLauncherIconVisible()) {
                    PackageManager p = context.getPackageManager();
                    ComponentName componentName = new ComponentName(context.getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    Intent appIntent = new Intent(context.getApplicationContext(), Splash.class);
                    appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(appIntent);
                }
            }
        }
        //WakeLock receiver
        WakeLocker.acquire(context);
        WakeLocker.release();
    }

    private boolean isLauncherIconVisible() {
        ComponentName componentName = new ComponentName(ctx.getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
        int enabledSetting = ctx.getPackageManager()
                .getComponentEnabledSetting(componentName);
        return enabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }
}
