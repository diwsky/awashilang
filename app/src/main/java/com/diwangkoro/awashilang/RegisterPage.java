package com.diwangkoro.awashilang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diwangkoro.awashilang.apiutils.MyApiAsyncTask;
import com.diwangkoro.awashilang.apiutils.SimpleHttpRequest;
import com.diwangkoro.awashilang.apiutils.UrlApi;
import com.diwangkoro.awashilang.gcm.MyInstanceIDListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 2/21/2016.
 */
public class RegisterPage extends Activity implements OnClickListener {

    EditText newEmail, newPassword, newEphone, newPhone;
    Button newRegister;
    SharedPreferences mPref;
    String myDeviceModel = android.os.Build.MODEL;

    String token="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage);
        mPref = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        newEmail = (EditText) findViewById(R.id.newEMAIL);
        newPassword = (EditText) findViewById(R.id.newPASSWORD);
        newPhone = (EditText) findViewById(R.id.newPHONE);
        newEphone = (EditText) findViewById(R.id.newEPHONE);


        //Masukkan variabel baru ke sharedpreference
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("newEMAIL", newEmail.getText().toString());
        editor.putString("newPASSWORD", newPassword.getText().toString());
        editor.putString("newPHONE", newPhone.getText().toString());
        editor.putString("newEPHONE", newEphone.getText().toString());


        if (mPref.getString("token", "").length()>0){
            mPref.edit().remove("token").commit();
        }

        Intent registerintent = new Intent(getApplicationContext(), MyInstanceIDListenerService.class);
        getApplicationContext().startService(registerintent);

        newRegister = (Button) findViewById(R.id.newREGISTER);
        newRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newREGISTER:

                if (newEmail.getText().toString().equals("")) {
                    alert(this, "Register", "FIll the Email column");
                } else if (newPassword.getText().toString().equals("")) {
                    alert(this, "Register", "FIll the Password column");
                } else if (newEphone.getText().toString().equals("")) {
                    alert(this, "Register", "FIll the Emergency Phone Number column");
                } else if (newPhone.getText().toString().equals("")) {
                    alert(this, "Register", "FIll the Emergency Phone Number column");
                } else {

                    SharedPreferences mmPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                    token = mmPref.getString("token", "");


                    HashMap<String, String> param = new HashMap<>();
                    param.put("registrationKey", token);
                    param.put("emailuser", newEmail.getText().toString());
                    param.put("passworduser", newPassword.getText().toString());
                    param.put("altnum", newEphone.getText().toString());
                    param.put("phonemodel", myDeviceModel);

                    Log.d("respon", token);
                    Log.d("model", myDeviceModel);

                    new MyApiAsyncTask(this, UrlApi._SAVEDATA, param, SimpleHttpRequest._POSTMETHOD) {

                        @Override
                        protected void onProgressUpdate(String response) throws Exception {

                            Log.e("respon", response);
                            final JSONObject jobj = new JSONObject(response);
                            if (jobj.getString("results").equals("ok")){
                                new AlertDialog.Builder(RegisterPage.this)
                                        .setTitle("Success!")
                                        .setMessage("Make sure your data is valid, you can change it later")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                                //---------simpan email ke sharedpref------------
//                                                SharedPreferences.Editor editor = mPref.edit();
//                                                try {
//                                                    editor.putString("EMAIL", jobj.getString("emailuser"));
//                                                    //editor.putString()
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                editor.commit();
                                                Intent ILogin = new Intent(RegisterPage.this, LoginPage.class);
                                                ILogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(ILogin);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                //alert(RegisterPage.this, "Success!", "Make sure your data is valid, you can change it later");
                            }else{
                                Toast.makeText(RegisterPage.this, jobj.getString("msg"), Toast.LENGTH_LONG).show();
                            }


                        }
                    }.execute();

                }
                break;
        }

    }

    public void alert(Context context, String title, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
