package com.diwangkoro.awashilang.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.diwangkoro.awashilang.R;
import com.diwangkoro.awashilang.apiutils.UrlApi;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by adityarizkiwicaksono on 3/9/16.
 */
public class MyInstanceIDListenerService extends IntentService {

    SharedPreferences mPref;
    String email, password, altnum;


    public MyInstanceIDListenerService() {
        super("register");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);

        mPref = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            mPref.edit().putString("token", token).commit();
            Log.e("tokensss_r", token);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
