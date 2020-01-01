package com.diwangkoro.awashilang.Monitoring;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.CallLog;
import android.telecom.Call;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by user on 4/1/2016.
 */
public class M_CallLogs {

    SharedPreferences mPref;

    public StringBuffer getLogs (Context context, String callType) {

        mPref = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);

        StringBuffer sb= new StringBuffer();
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int counter =0;
        int SUM_CALL = mPref.getInt("SUM_CALL", 5);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy_HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM_HH:mm");

        cursor.moveToLast();
        do {
            String numId = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
            String raw_date = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
            Date callDate = new Date(Long.valueOf(raw_date));
            String date = sdf.format(callDate);
            String date2 = sdf.format(callDate);

            switch (callType) {
                case "outgoing":
                    if (Integer.parseInt(type) == CallLog.Calls.OUTGOING_TYPE) {
                        sb.append(numId+"/"+date2+"/"+duration+"s\n");
                        //sb.append(numId+"/"+date+"\n");
                        counter+=1;
                    }
                    break;

                case "incoming":
                    if (Integer.parseInt(type) == CallLog.Calls.INCOMING_TYPE) {
                        sb.append(numId+"/"+date2+"/"+duration+"s\n");
                        //sb.append(numId+"/"+date+"\n");
                        counter+=1;
                    }
                    break;

                case "missed":
                    if (Integer.parseInt(type) == CallLog.Calls.MISSED_TYPE) {
                        sb.append(numId+"/"+date+"\n");
                        counter+=1;
                    }
                    break;
            }
        } while (cursor.moveToPrevious() && counter != SUM_CALL);

        cursor.close();
        return sb;
    }

    public StringBuffer getLogstoEmail (Context context, String callType) {

        mPref = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);

        StringBuffer sb= new StringBuffer();
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int counter =0;
        int SUM_CALL = mPref.getInt("SUM_CALL", 5);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");

        cursor.moveToLast();
        do {
            String numId = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
            String raw_date = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
            Date callDate = new Date(Long.valueOf(raw_date));
            String date = sdf.format(callDate);

            switch (callType) {
                case "outgoing":
                    if (Integer.parseInt(type) == CallLog.Calls.OUTGOING_TYPE) {
                        sb.append("-------------------------------\n");
                        sb.append("Nomor telepon: "+numId+"\n"+
                                "Tanggal panggilan: "+date+"\n"+
                                "Durasi Panggilan: "+duration+"s\n");
                        sb.append("-------------------------------\n");
                        //sb.append(numId+"/"+date+"\n");
                        counter+=1;
                    }
                    break;

                case "incoming":
                    if (Integer.parseInt(type) == CallLog.Calls.INCOMING_TYPE) {
                        sb.append("-------------------------------\n");
                        sb.append("Nomor telepon: "+numId+"\n"+
                                "Tanggal panggilan: "+date+"\n"+
                                "Durasi Panggilan: "+duration+"s\n");
                        sb.append("-------------------------------\n");
                        //sb.append(numId+"/"+date+"\n");
                        counter+=1;
                    }
                    break;

                case "missed":
                    if (Integer.parseInt(type) == CallLog.Calls.MISSED_TYPE) {
                        sb.append("-------------------------------\n");
                        sb.append("Nomor telepon: "+numId+"\n"+
                                "Tanggal panggilan: "+date+"\n");
                        sb.append("-------------------------------\n");
                        counter+=1;
                    }
                    break;
            }
        } while (cursor.moveToPrevious() && counter != 10);

        cursor.close();
        return sb;
    }
}