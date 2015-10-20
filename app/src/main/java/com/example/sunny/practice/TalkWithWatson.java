package com.example.sunny.practice;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sunny on 10/7/15.
 */
public class TalkWithWatson implements Runnable {


    private MyActivity myActivity;

    public static String sQuestion;
    private final String mLogTag = "Watson: ";
    private String mWatsonQueryString = "";
    private String mWatsonAnswerString = "";
    private boolean mIsQuerying = false;

    private SSLContext context;
    private HttpsURLConnection connection;
    private String jsonData;



    public TalkWithWatson(MyActivity myActivity)
    {

        this.myActivity = myActivity;
    }

    private String getEncodedValues(String user_id, String user_password) {
        String textToEncode = user_id + ":" + user_password;
        byte[] data = null;
        try {
            data = textToEncode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

    @Override
    public void run(){

        Log.i("Myactivity", "INside thread");
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        myActivity.setCurrentThread(Thread.currentThread());

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};



        // establish SSL trust (insecure for demo)
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (java.security.KeyManagementException e) {
            e.printStackTrace();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {

            Log.i("Myactivity", "Connecting...");
            // Default HTTPS connection option values
            URL watsonURL = new URL("https://watson-wdc01.ihost.com/instance/530/deepqa/v1/question");
            int timeoutConnection = 30000;
            connection = (HttpsURLConnection) watsonURL.openConnection();
            connection.setSSLSocketFactory(context.getSocketFactory());
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(timeoutConnection);
            connection.setReadTimeout(timeoutConnection);
            Log.i("Myactivity", "Seting http.");
            // Watson specific HTTP headers
            connection.setRequestProperty("X-SyncTimeout", "30");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + getEncodedValues("utep_student7", "xrOv9LH3"));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cache-Control", "no-cache");
            Log.i("Myactivity", "Asigning value" + TalkWithWatson.sQuestion);
            mWatsonQueryString = TalkWithWatson.sQuestion;
            Log.i("Myactivity", "Asigning value"+ mWatsonQueryString);
            Log.i("Myactivity", "output stream enter");
            OutputStream out = connection.getOutputStream();
            Log.i("Myactivity", "output stream exit");
            String query = "{\"question\": {\"questionText\": \"" + mWatsonQueryString + "\"}}";
            out.write(query.getBytes());
            Log.i("Myactivity", "query sent");
            out.close();

        } catch (IOException e) {
            Log.i("Myactivity", "Exception lalala");
            e.printStackTrace();
        }

        int responseCode;
        try {

            Log.i("Myactivity", "Response received");
            if (connection != null) {
                responseCode = connection.getResponseCode();
                Log.i(mLogTag, "Server Response Code: " + Integer.toString(responseCode));

                switch(responseCode) {
                    case 200:
                        Log.i("Myactivity", "Response 200");
                        // successful HTTP response state
                        InputStream input = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while((line = reader.readLine()) != null) {
                            response.append(line);
                            response.append('\r');

                        }
                        reader.close();

                        Log.i(mLogTag, "Watson Output: " + response.toString());
                        jsonData = response.toString();

                        break;
                    default:
                        Log.i("Myactivity", "Response default");
                        break;
                }
            }
        } catch (IOException e) {
            Log.i("Myactivity", "Exception occured");
            e.printStackTrace();
        }

        try {

            // received data, deliver JSON to PostExecute
            if (jsonData != null) {
                JSONObject watsonResponse = new JSONObject(jsonData);
                JSONObject question = watsonResponse.getJSONObject("question");
                JSONArray evidenceArray = question.getJSONArray("evidencelist");
                JSONObject mostLikelyValue = evidenceArray.getJSONObject(0);
                mWatsonAnswerString = mostLikelyValue.get("text").toString();
                Log.i("Myactivity", "mWatsonAnswerString :" + mWatsonAnswerString);

                if (!mWatsonAnswerString.equals(null) && !mWatsonAnswerString.equals("")) {
                    Log.i("Myactivity", "Sending msg  :" + mWatsonAnswerString);
                    Message msgObj = MyActivity.mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", mWatsonAnswerString);
                    msgObj.setData(b);
                    MyActivity.mHandler.sendMessage(msgObj);
                    Log.i("Myactivity", "Sent  :" + mWatsonAnswerString);
                }
                else
                {
                    Message msgObj = MyActivity.mHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", "");
                    msgObj.setData(b);
                    MyActivity.mHandler.sendMessage(msgObj);
                }


            }
        }
        catch (Exception ex)
        {

        }

        // else, hit HTTP error, handle in PostExecute by doing null check





    }
}
