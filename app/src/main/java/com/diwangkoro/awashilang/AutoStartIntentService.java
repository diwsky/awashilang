package com.diwangkoro.awashilang;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import com.diwangkoro.awashilang.Monitoring.M_CallLogs;
import com.diwangkoro.awashilang.Monitoring.M_capture;
import com.diwangkoro.awashilang.Monitoring.M_message;
import com.diwangkoro.awashilang.Tracking.MyLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogRecord;

import javax.mail.internet.MimeMessage;

/**
 * Created by user on 5/21/2016.
 */
public class AutoStartIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public AutoStartIntentService() {
        super("AutoStartIntentService");
    }
    String curPhoneNum="";
    String coordinat="";
    SharedPreferences mPref;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy; HH:mm");
    private String curDate = sdf.format(new Date());
    StringBuffer emailString = new StringBuffer();
    String track="";


    @Override
    protected void onHandleIntent(Intent intent) {

        mPref = getSharedPreferences(getPackageName().toString(), Context.MODE_PRIVATE);

        String intentType = intent.getExtras().getString("caller");
        if (intentType == null) return;
        if (intentType.equals("RebootReceiver")) {

            /**
             * Menyalakan foreground service untuk siap menerima perintah dari pengguna
             */
            //Intent foregroundService = new Intent(this, Service_General.class);
            //getApplicationContext().startService(foregroundService);

            /**
             * Akuisisi Data pada smartphone
             */
            //---Tracking---
            final MyLocation myLocation = new MyLocation();
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    String lat = String.valueOf(location.getLatitude());
                    String lng = String.valueOf(location.getLongitude());
                    float accuracy = location.getAccuracy();

                    track = "maps.google.com/maps?q="+ lat+","+lng+" with accuracy "+accuracy+" meters";

                    sendAllData();

                    //Saving the timestamp & last location
                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    mPref.edit().putString("lastLAT", lat).apply();
                    mPref.edit().putString("lastLNG", lng).apply();
                }
            };
            myLocation.getLocation(getApplicationContext(), locationResult);

            if (!myLocation.hasService) {
                track = "Fitur GPS anda tidak aktif";
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendAllData();
                    }
                });
            }
        }
    }

    public void sendAllData() {
        /**
         * Prosedur yang berfungsi untuk mengirimkan data-data smartphone ke email pengguna
         * saat smartphone baru dinyalakan
         */

        String emailUser = mPref.getString("EMAIL","diwangkoro270@gmail.com");
        if (!emailUser.equalsIgnoreCase("")) {

            M_CallLogs mCalLogs = new M_CallLogs();
            M_message mMessage = new M_message();
            M_capture mCapture = new M_capture();
            TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

            emailString.append("\n\n==========PHONE NUMBER=========\n");
            curPhoneNum = tMgr.getLine1Number();
            if (curPhoneNum.equalsIgnoreCase("")) {
                curPhoneNum = "Nomor kartu anda belum terdaftar pada smartphone.";}
            emailString.append(curPhoneNum+"\n");
            emailString.append("\n\n===========TRACKING============\n");
            emailString.append(track+"\n");
            emailString.append("\n\n===========INCOMING============\n");
            emailString.append(mCalLogs.getLogstoEmail(getApplicationContext(),"incoming"));
            emailString.append("\n\n===========OUTGOING============\n");
            emailString.append(mCalLogs.getLogstoEmail(getApplicationContext(),"outgoing"));
            emailString.append("\n\n============MISSED=============\n");
            emailString.append(mCalLogs.getLogstoEmail(getApplicationContext(),"missed"));
            emailString.append("\n\n============INBOX==============\n");
            emailString.append(mMessage.getSMStoEmail(getApplicationContext(),"inbox"));
            emailString.append("\n\n============SENT===============\n");
            emailString.append(mMessage.getSMStoEmail(getApplicationContext(),"sent"));
            emailString.append("\n\n===========BATTERY=============\n\n");
            emailString.append(batteryLevel()+"%\n");

            mCapture.takeSnapShots(getApplicationContext());
            sendEmail(getApplicationContext(),"AwasHilang! Data smartphone pada "+curDate,String.valueOf(emailString));
        }
    }

    public String batteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null,ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);

        return String.valueOf(level);
    }
    
    public void sendEmail(Context context, String subject, String body) {
        M_capture capture = new M_capture();

        //---------Sending image to email------//
        Mail m = new Mail("awashilang.apps@gmail.com", "diditganteng");
        String[] toArr = {mPref.getString("EMAIL","diwangkoro270@gmail.com")};
        m.setTo(toArr);
        m.setFrom("awashilang.apps@gmail.com");
        m.setSubject(subject);
        m.setBody(body);

        try  {
            try {
               m.addAttachment("/sdcard/Awas Hilang!/" + capture.getImageDate() + ".png");
            } catch (Exception e) {

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

    public void checkPhoneNumber() {

        //Akuisisi nomor telpon yang digunakan smartphone

        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        int tempLength = tMgr.getLine1Number().length();
        curPhoneNum = tMgr.getLine1Number();

        Log.e("Got the phone number",curPhoneNum);

        //Akuisisi nomor telpon user
        String idPhonenum = mPref.getString("phoneNum","085711176236");
        int tempLength2 = idPhonenum.length();
        idPhonenum = idPhonenum.substring(tempLength2-4, tempLength2);
        Receiver_SMS rSMS = new Receiver_SMS();

        if (!curPhoneNum.equalsIgnoreCase("")) {
            curPhoneNum.substring(tempLength-4, tempLength);
            if (curPhoneNum != idPhonenum) {
                rSMS.sendSMS(getApplicationContext(), mPref.getString("emergencyPhone","085711176236"), "Your current phone num: "+curPhoneNum);
            }
        } else {
            rSMS.sendSMS(getApplicationContext(), mPref.getString("emergencyPhone","085711176236"), "Your new number was not registered");
            Log.d("sent!!","yeah....................");
        }
    }
}
