package com.diwangkoro.awashilang.apiutils;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hp on 3/29/2016.
 */
public class SimpleHttpRequest {

    InputStream inputStream = null;
    String apiresponse = "";
    public static int _POSTMETHOD = 0;
    public static int _GETMETHOD = 1;

    public String getApiResponseFromUrl(String url, Map<String, String> params, int reqMethod) {

        // Making HTTP request

        if (reqMethod==_POSTMETHOD) {
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                httpPost.setEntity(new UrlEncodedFormEntity(endcodePostParameter(params)));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (reqMethod==_GETMETHOD){
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url + encodeGetParameter(params));
                Log.e("httpget", url + encodeGetParameter(params));

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            inputStream.close();
            apiresponse = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return apiresponse;
    }

    public List<BasicNameValuePair> endcodePostParameter(Map<String, String> params) {
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String value = params.get(key);
            nameValuePairs.add(new BasicNameValuePair(key, value));
        }
        return nameValuePairs;
    }

    public String encodeGetParameter(Map<String, String> params) {
        StringBuilder buff = new StringBuilder("?");
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String value = params.get(key);
            buff.append(key).append("=").append(URLEncoder.encode(value)).append("&");
        }
        return buff.toString();
    }

}
