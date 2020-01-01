package com.diwangkoro.awashilang.gcm;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.diwangkoro.awashilang.Alerting.Service_Alarm;
import com.diwangkoro.awashilang.Tracking.MyLocation;
import com.diwangkoro.awashilang.WakeLocker;
import com.diwangkoro.awashilang.apiutils.UrlApi;
import com.google.android.gms.gcm.GcmListenerService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by adityarizkiwicaksono on 3/9/16.
 */
public class MyGcmListenerServiceAlarm extends GcmListenerService {

    SharedPreferences mPref;
    String lock_code="";

    public static DevicePolicyManager deviceManager;
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        mPref = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);

        String pesan = data.getString("request");
        //Log.d("listener", pesan);

        deviceManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (pesan.equals("alarm")) {
            Intent intent = new Intent(this, Service_Alarm.class);
            startService(intent);

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._resp);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("respon=" + respon + "&registrationKey=" + token);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save ", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } else if (pesan.equals("lock")) {
            Command_gcm mLocking = new Command_gcm();
            mLocking.locknow(getApplicationContext());

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._resp);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("respon=" + respon + "&registrationKey=" + token);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save ", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();


        } else if (pesan.equals("hidden")) {
            Command_gcm mHidden = new Command_gcm();
            mHidden.hideLauncherIcon(getApplicationContext());

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._resp);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("respon=" + respon + "&registrationKey=" + token);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save ", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();


        } else if (pesan.equals("unhide")) {
            Command_gcm mHidden = new Command_gcm();
            mHidden.unhideLauncherIcon(getApplicationContext());

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._resp);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("respon=" + respon + "&registrationKey=" + token);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save ", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }else if (pesan.equals("capture")){
            Command_gcm mCapture = new Command_gcm();
            mCapture.takeSnapShots(getApplicationContext());
            mCapture.sendEmail(getApplicationContext(),"This is picture we caught using your front camera","Go find some help!","photo");

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._resp);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("respon=" + respon + "&registrationKey=" + token);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save ", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();


        } else if (pesan.equals("batt")) {
            final Command_gcm mBattery = new Command_gcm();
            final String battery = String.valueOf(mBattery.mBatteryLevel(this));

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._batt);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("baterai=" + battery + "&registrationKey=" + token + "&respon=" + respon);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save battery", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }else if (pesan.equals("smsout")) {
            final Command_gcm mSent = new Command_gcm ();
            final String smsoutbox = mSent.getSMS(this,"sent");

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._smsout);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("showsmsoutsave=" + smsoutbox + "&registrationKey=" + token  + "&respon=" + respon);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save sms inbox", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } else if (pesan.equals("smsin")) {
            final Command_gcm M_inbox2 = new Command_gcm ();
            final String smsinbox = M_inbox2.getSMS(this,"inbox");
            Log.d("response save sms inbox", smsinbox);

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._smsin);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("showsmsinsave=" + smsinbox + "&registrationKey=" + token  + "&respon=" + respon);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();

                        Log.d("response save sms inbox", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } else if (pesan.equals("call")) {
            final Command_gcm M_CallLogs = new Command_gcm();
            final String calllog = M_CallLogs.getLogs(this);

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._calllog);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("calllog=" +calllog + "&registrationKey=" + token  + "&respon=" + respon);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save call log", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } else if(pesan.equals("sim")) {
            final Command_gcm simcard = new Command_gcm();
            final String getNumber = simcard.getNumber(this);

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._sim);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("showsimsave=" +getNumber + "&registrationKey=" + token  + "&respon=" + respon);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save call log", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        else if (pesan.equals("track")) {

            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                            String lat, lng;

                            @Override
                            public void gotLocation(Location location) {
                                lat = String.valueOf(location.getLatitude());
                                lng = String.valueOf(location.getLongitude());
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            String token = mPref.getString("token", "");
                                            String respon = "1";
                                            Log.d("getToken", token);
                                            URL url = new URL(UrlApi._saveloc);
                                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                            connection.setRequestMethod("POST");
                                            connection.setDoOutput(true);
                                            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                                            dataOutputStream.writeBytes("gmapslocation=https://maps.google.com/maps?q=" + lat + "," + lng + "&registrationKey=" + token  + "&respon=" + respon);
                                            dataOutputStream.flush();
                                            dataOutputStream.close();

                                            String responseCode = connection.getResponseMessage();
                                            Log.d("response save location", responseCode);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                            }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);

        }
        else {

            //Lock dengan kode tertentu;
            deviceManager.resetPassword(pesan, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
            mPref.edit().putString("LOCK_PASS", pesan).apply();
            deviceManager.lockNow();

            new Thread() {
                @Override
                public void run() {
                    try {
                        //String token = "ccXjW5l0p6k:APA91bGJfDe5pqIn36ipE47LTmgj4sdWDv4emkqGC0D1KVrGo2nIK8vpodyWzk-1yhBa5mnlmIqJWR3gvSJjrbukpHmJshVOmCHDDHqDBNCG4uu9r1YWb5p8uCYBOKo8Z80wTzcmYZZP";
                        String token = mPref.getString("token", "");
                        String respon = "1";
                        Log.d("getToken", token);
                        URL url = new URL(UrlApi._resp);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                        dataOutputStream.writeBytes("respon=" + respon + "&registrationKey=" + token);
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        String responseCode = connection.getResponseMessage();
                        Log.d("response save ", responseCode);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        WakeLocker.acquire(getApplicationContext());
        WakeLocker.release();




    }


}