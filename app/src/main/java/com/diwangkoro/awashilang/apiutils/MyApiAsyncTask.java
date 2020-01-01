package com.diwangkoro.awashilang.apiutils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;

/**
 * Created by hp on 3/29/2016.
 */
public abstract class MyApiAsyncTask extends AsyncTask<Void, Void, Void> {
    Context ctx;
    String url;
    Map<String, String> params;
    int reqMethod;

    String response = "";
    private volatile boolean running = true;
    ProgressDialog ringProgressDialog;

    public MyApiAsyncTask(Context ctx, String url, Map<String, String> params, int reqMethod) {
        this.ctx = ctx;
        this.url = url;
        this.params = params;
        this.reqMethod = reqMethod;
    }


    @Override
    protected void onPreExecute() {
        ringProgressDialog = ProgressDialog.show(ctx, null, "Loading ...", true);
        ringProgressDialog.setCancelable(false);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            onCancelled();
            onProgressUpdate(response);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onCancelled() {
        running = false;
        try {
            ringProgressDialog.dismiss();
        } catch (Exception e) {
        }

    }

    @Override
    protected Void doInBackground(Void... param) {
        while (running) {
            if (params == null){
             break;
            }else {

                Log.e("url", url);
                SimpleHttpRequest jParser = new SimpleHttpRequest();
                response = jParser.getApiResponseFromUrl(url, this.params, reqMethod);

                System.out.println("Response : " + response);
                running = false;
            }
        }
        return null;

    }

    protected abstract void onProgressUpdate(String response) throws Exception;



}
