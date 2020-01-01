package com.diwangkoro.awashilang.Monitoring;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import java.util.Date;

/**
 * Created by user on 3/31/2016.
 */
public class M_message {

    SharedPreferences mPref;

    public StringBuffer getSMS(Context context, String folder) {

        mPref = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);

        StringBuffer sb = new StringBuffer();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"+folder), null, null, null, null);
        int counter = 0;
        int SUM_SMS = mPref.getInt("SUM_SMS",5);

        cursor.moveToFirst();
        do {
            String numId = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            sb.append(numId+ ":");
            if (body.length() < 10) {
                sb.append(body);
            } else {
                sb.append(body.substring(0,9));
            }
            sb.append("\n");
            counter +=1;
        } while (cursor.moveToNext() && counter !=SUM_SMS);

        cursor.close();

        return sb;
    }

    public StringBuffer getSMStoEmail(Context context, String folder) {

        StringBuffer sb = new StringBuffer();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"+folder), null, null, null, null);
        int counter = 0;

        cursor.moveToFirst();
        do {
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            Date callDayTime = new Date(Long.valueOf(date));

            sb.append("------------------------------------------------------");
            sb.append("\nNumber: " + address + "\nDate: " + callDayTime+ "\nMessage: \n" + body);
            sb.append("\n------------------------------------------------------\n");
            counter += 1;

        } while (cursor.moveToNext() && counter != 10);
        cursor.close();

        return sb;
    }
}
