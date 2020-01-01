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
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.diwangkoro.awashilang.apiutils.MyApiAsyncTask;
import com.diwangkoro.awashilang.apiutils.SimpleHttpRequest;
import com.diwangkoro.awashilang.apiutils.UrlApi;
import com.diwangkoro.awashilang.gcm.MyInstanceIDListenerService;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by user on 2/21/2016.
 */
public class LoginPage extends Activity implements OnClickListener {

    EditText email, password;
    Button login, register;
    SharedPreferences mPref;
    //Variabel sementara
    String EMAILCHECK = "diwang@gmail.com", PASSWORDCHECK = "1234";
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);


        email = (EditText) findViewById(R.id.USERNAME);
        password = (EditText) findViewById(R.id.PASSWORD);
        login = (Button) findViewById(R.id.LOGIN);
        register = (Button) findViewById(R.id.REGISTER);

        mPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        if (mPref.getString("token", "").length()>0){
            mPref.edit().remove("token").commit();
        }

        Intent registerintent = new Intent(getApplicationContext(), MyInstanceIDListenerService.class);
//        registerintent.putExtra("spesial", "");
        getApplicationContext().startService(registerintent);


        if (mPref.getString("EMAIL", "").length() > 0) {
            Intent Imenu = new Intent(this, Settings.class);
            startActivity(Imenu);
            finish();
        }
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.LOGIN:

                if (email.getText().toString().equals("")) {
                    Toast.makeText(LoginPage.this, "email tidak boleh kosong", Toast.LENGTH_LONG).show();

                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(LoginPage.this, "password tidak boleh kosong", Toast.LENGTH_LONG).show();

                } else {

                    SharedPreferences mmPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                    token = mmPref.getString("token", "");

                    Log.e("tokensss", "inidia = " + token);

                    HashMap<String, String> param = new HashMap<>();
                    param.put("emailuser", email.getText().toString());
                    param.put("passworduser", password.getText().toString());
                    param.put("token", token);
                    new MyApiAsyncTask(this, UrlApi._loginapps, param, SimpleHttpRequest._POSTMETHOD) {
                        @Override
                        protected void onProgressUpdate(String response) throws Exception {
                            String apa = "RESPONSE:\n" + response + "\n\n";
                            JSONObject jobj = new JSONObject(response);
                            if (jobj.getString("results").equalsIgnoreCase("ok")) {
                                SharedPreferences.Editor editor = mPref.edit();
                                editor.putString("EMAIL", jobj.getString("emailuser"));
                                //editor.putString()
                                editor.commit();
                                Intent Imenu = new Intent(LoginPage.this, Settings.class);
                                startActivity(Imenu);
                                finish();

                            }else {
                                Toast.makeText(LoginPage.this, jobj.getString("msg"), Toast.LENGTH_LONG).show();

                            }
                        }
                    }.execute();

                }

                break;

            case R.id.REGISTER:
                Intent Iregister = new Intent(this, RegisterPage.class);
                startActivity(Iregister);
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
