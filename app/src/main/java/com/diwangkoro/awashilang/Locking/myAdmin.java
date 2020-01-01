package com.diwangkoro.awashilang.Locking;

import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.diwangkoro.awashilang.Mail;
import com.diwangkoro.awashilang.Monitoring.M_capture;
import com.diwangkoro.awashilang.Alerting.Service_Alarm;
import com.diwangkoro.awashilang.WakeLocker;

/**
 * Created by user on 4/4/2016.
 */
public class myAdmin extends DeviceAdminReceiver {

    SharedPreferences mPref;


    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context,"Sample Device Admin: enabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "This is an optional message to warn the user about disabling.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context,"Sample admin disabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        showToast(context,"Password berhasil diubah!");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {

        mPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        int no = mPref.getInt("WRONG_INPUT",0) +1;
        showToast(context.getApplicationContext(),"Percobaan salah password= "+no );
        int attempt = mPref.getInt("ATTEMPT",3);

        if (no >= attempt) {
            M_capture capture = new M_capture();
            capture.takeSnapShots(context);
            sendEmail(context,"This is picture we caught using your front camera",
                    "This person tried to unlock your phone!",
                    "photo");
            try {
                intent = new Intent(context.getApplicationContext(), Service_Alarm.class);
                context.startService(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            mPref.edit().putInt("WRONG_INPUT",no).apply();
        }

        WakeLocker.acquire(context);
        WakeLocker.release();

    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {

        showToast(context,"Password berhasil!");
        mPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        mPref.edit().putInt("WRONG_INPUT",0).apply();
        if (isMyServiceRunning(context,Service_Alarm.class)) {
            context.stopService(new Intent(context,Service_Alarm.class));
        }

        WakeLocker.acquire(context);
        WakeLocker.release();
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
                Toast.makeText(context.getApplicationContext(), "Email was sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getApplicationContext(), "Email was not sent", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ReceiverSMS", "Could not send email", e);
        }
    }
}

