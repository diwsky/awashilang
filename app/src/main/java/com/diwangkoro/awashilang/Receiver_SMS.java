package com.diwangkoro.awashilang;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.diwangkoro.awashilang.Alerting.Service_Alarm;
import com.diwangkoro.awashilang.Monitoring.M_CallLogs;
import com.diwangkoro.awashilang.Monitoring.M_capture;
import com.diwangkoro.awashilang.Monitoring.M_message;
import com.diwangkoro.awashilang.Tracking.MyLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 1/27/2016.
 */
public class Receiver_SMS extends BroadcastReceiver {

    String sender;
    String lat="",lng="";
    String command, lock_code="";
    SharedPreferences mPref,prefs;
    DevicePolicyManager deviceManager;
    boolean isEmailReceive;
    float accuracy;

    @Override
    public void onReceive(final Context context, Intent intent) {

        //Persiapan ambil SMS masuk
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String messageReceived = "";
        String[] parts;
        mPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        deviceManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        if (bundle != null) {
            //Akuisisi SMS yang masuk
            Object[] pdus = (Object[]) bundle.get("pdus"); //Akuisisi SMS masuk
            msgs = new SmsMessage[pdus.length];


            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                messageReceived += msgs[i].getMessageBody().toString().toLowerCase();
            }

            if (messageReceived.contains(" ")) {
                //Apabila SMS berisi perintah lock dengan code
                parts = messageReceived.split(" ");
                command = parts[0];
                lock_code = parts[1];

            } else {
                //Apabila perintah lain selain lock
                command = messageReceived;
            }

            //Display the new message
            Toast.makeText(context, command, Toast.LENGTH_SHORT).show();
            Toast.makeText(context, lock_code, Toast.LENGTH_SHORT).show();

            //Acquire the sender
            sender = msgs[0].getOriginatingAddress();

        }

        /*** Variabel pingin ada pemberitahuan ke email juga atau nggak ***/
        isEmailReceive = mPref.getBoolean("EMAIL_RECEIVE",false);

        /***List Command***/
        if (command.equals(prefs.getString("Track","track"))) {
            //deleteSMS(context,"inbox", messageReceived);
            //Call tracking
            final MyLocation myLocation = new MyLocation();
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());
                    accuracy = location.getAccuracy();

                    String text = "maps.google.com/maps?q="+ lat+","+lng+" with accuracy "+accuracy+" meters";

                    sendSMS(context, sender, text);

                    //Saving the timestamp & last location
                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    mPref.edit().putString("lastLAT", lat).apply();
                    mPref.edit().putString("lastLNG", lng).apply();

                    if (isEmailReceive){
                        sendEmail(context,"Your smartphone location",
                                "maps.google.com/maps?q="+ lat+","+lng+" with accuracy "+accuracy+" meters",
                                "none");
                    }
                }
            };
            myLocation.getLocation(context, locationResult);
            if (!myLocation.hasService) {
                sendSMS(context, sender, "Fitur GPS anda tidak aktif");
            }
        } else

        if (command.equals(prefs.getString("Alert","alert"))) {
            Intent tes = new Intent(context, Service_Alarm.class);
            context.startService(tes);
        }else

        if (command.equals(prefs.getString("Lock","lock"))) {
            if (lock_code.equalsIgnoreCase("")) {
                deviceManager.lockNow();
            } else {
                //Lock dengan kode tertentu;
                deviceManager.resetPassword(lock_code, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                mPref.edit().putString("LOCK_PASS", lock_code).apply();
                deviceManager.lockNow();
            }
        }else

        if (command.equals(prefs.getString("Alock","alock"))) {
            if (lock_code.equalsIgnoreCase("")) {
                deviceManager.lockNow();
            } else {
                //Lock dengan kode tertentu;
                deviceManager.resetPassword(lock_code, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                mPref.edit().putString("LOCK_PASS", lock_code).apply();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent iAlarm = new Intent(context,Service_Alarm.class);
                        context.startService(iAlarm);
                    }
                },10000);
                deviceManager.lockNow();
            }
        }else

        if (command.equals(prefs.getString("Inbox","inbox"))) {
            M_message mInbox = new M_message();
            sendSMS(context, sender, mInbox.getSMS(context, "inbox").toString());
            if (isEmailReceive){
                sendEmail(context,"This is your inbox data",mInbox.getSMStoEmail(context, "inbox").toString(),"none");
            }
        }else

        if (command.equals(prefs.getString("Sent","sent"))) {
            M_message mSent = new M_message();
            sendSMS(context, sender, mSent.getSMS(context, "sent").toString());
            if (isEmailReceive){
                sendEmail(context,"This is your inbox data",mSent.getSMStoEmail(context, "sent").toString(),"none");
            }
        }else

        if (command.equals(prefs.getString("Capture","capture"))) {
            M_capture capture = new M_capture();
            capture.takeSnapShots(context);
            sendEmail(context,"This is picture we caught using your front camera","Go find some help!","photo");
        }else

        if (command.equals(prefs.getString("Battery","battery"))) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.getApplicationContext().registerReceiver(null,ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            sendSMS(context,sender,String.valueOf(level)+"%");
            if (isEmailReceive){
                sendEmail(context,"This is your current battery level",String.valueOf(level)+"%","none");
            }
        }else

        if (command.equals(prefs.getString("Incoming","incoming"))) {
            M_CallLogs mILogs = new M_CallLogs();
            sendSMS(context,sender,String.valueOf(mILogs.getLogs(context, "incoming")));
            if (isEmailReceive){
                sendEmail(context,"This is your Incoming Call logs",String.valueOf(mILogs.getLogstoEmail(context, "incoming")),"none");
            }
        }else

        if (command.equals(prefs.getString("Outgoing","outgoing"))) {
            M_CallLogs mOLogs = new M_CallLogs();
            sendSMS(context, sender, String.valueOf(mOLogs.getLogs(context, "outgoing")));
            if (isEmailReceive){
                sendEmail(context,"This is your Outgoing Call logs",String.valueOf(mOLogs.getLogstoEmail(context, "incoming")),"none");
            }
        }else

        if (command.equals(prefs.getString("Missed","missed"))) {
            M_CallLogs mMLogs = new M_CallLogs();
            sendSMS(context,sender,String.valueOf(mMLogs.getLogs(context, "missed")));
            if (isEmailReceive){
                sendEmail(context,"This is your Missed Call logs",String.valueOf(mMLogs.getLogstoEmail(context, "incoming")),"none");
            }
        }else

        if (command.equals(prefs.getString("Hide","hide"))) {
            hideLauncherIcon(context);
        }

        //WakeLock receiver
        WakeLocker.acquire(context);
        WakeLocker.release();
    }

    private void hideLauncherIcon(Context context) {
        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getApplicationContext().getPackageName().toString(), com.diwangkoro.awashilang.Splash.class.getName().toString());
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        mPref.edit().putBoolean("ICON_STATUS", false).apply();
        Log.e("Diwang", mPref.getString("LAUNCHER_NUMBER","12345"));
    }

    public void sendSMS(Context context, String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void deleteSMS (Context context, String folder,  String msg) {
        try {
            Uri uriSMS = Uri.parse("content://sms/"+folder);
            Cursor c = context.getContentResolver().query(uriSMS,new String[] {
                    "_id", "address", "body"},null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    String id = c.getString(0);
                    String address = c.getString(1);
                    String body = c.getString(2);

                    if (body.equals(msg) /*&& address.equals(phoneNo)*/) {
                        Log.d("DELETING SMS", "Akan mendelete SMS!!");
                        Log.d("Isi dari SMS", body);
                        try {
                            context.getContentResolver().delete(Uri.parse("content://sms/"+id),null,null);
                            Log.d("BERHASIL DELETE", "BERHASIL DELETE AYYEEE");
                        } catch (Exception e) {
                            Log.e("Gagal delete sms", ":''''''''''''''''");
                        }
                    }
                } while (c.moveToNext());
            }
        } catch (Exception ex) {
            Log.e("ERROR","Errorr di try catch");
            ex.printStackTrace();
        }
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
