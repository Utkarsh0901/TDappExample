package com.example.utkarshm.dbapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class FollowActivity extends AppCompatActivity {

    String message = "";
    String uid ="";
    Context c;
    String[] astring;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        //Log.e("asfda","message");
        c = this.getBaseContext();
        Button follow = (Button)findViewById(R.id.follow_button);
        Button showpost = (Button)findViewById(R.id.post_button);
        Button cancel = (Button)findViewById(R.id.cancel_button);
        //cancel.setBackgroundColor(16711680);

        final Intent intent = getIntent();
        message = intent.getStringExtra("FOLLOW");
        uid = intent.getStringExtra("UID");

        astring = message.split(" : ");

        //Log.e("asfda",message);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FollowUser().execute();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, HomeActivity.class);
                intent.putExtra("UID",uid);

                startActivity(intent);

            }
        });
        showpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, FollowShowPostActivity.class);
                intent.putExtra("FOLLOWUID",message);
                intent.putExtra("UID",uid);

                startActivity(intent);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuitems,menu);
        return true;
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.maddpost:
                Intent intent1 = new Intent(c, AddPostActivity.class);
                intent1.putExtra("UID", uid);
                startActivity(intent1);
                return true;
            case R.id.mlogout:
                new FollowActivity.Logout().execute();
                return true;
            case R.id.mseepost:
                Intent intent = new Intent(c, HomeActivity.class);
                intent.putExtra("UID",uid);
                startActivity(intent);
                return true;
            case R.id.msearch:
                new FollowActivity.Search().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class FollowUser extends AsyncTask<String, Void, String> {



        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject param = new JSONObject();
            try {
                param.put("uid",astring[0]);
                //param.put("password",pid);
                Log.e("params",param.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("te","as1");
            }
            try {


                URL url = new URL(Url.Baseurl+"Follow"); // here is your URL path

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(param));

                writer.flush();
                writer.close();
                os.close();


                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    Log.e("A", sb.toString());
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

            JSONObject jObject  = null; // json
            //JSONObject temp  = null; // json

            String status1 = "";
            //String data = "";
            try {
                jObject = new JSONObject(result);
                //JSONObject status = jObject.getJSONObject("status"); // get data object
                status1 = jObject.getString("status"); // get the name from data.
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status1.compareTo("true") == 0){
                Toast.makeText(getApplicationContext(), "User Followed" ,
                        Toast.LENGTH_LONG).show();
            }else{

                Toast.makeText(getApplicationContext(), "Already Followed" ,
                        Toast.LENGTH_LONG).show();
            }
        }

    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    public class Logout extends AsyncTask<String, Void, String> {



        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            try {


                URL url = new URL(Url.Baseurl+"Logout"); // here is your URL path

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                //conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    Log.e("A", sb.toString());
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

            JSONObject jObject  = null; // json
            JSONObject temp  = null; // json

            String status1 = "";
            //String data = "";
            try {
                jObject = new JSONObject(result);
                //JSONObject status = jObject.getJSONObject("status"); // get data object
                status1 = jObject.getString("status"); // get the name from data.
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status1.compareTo("true") == 0){
                Intent intentq = new Intent(c, MainActivity.class);

                c.startActivity(intentq);
            }else{

                Toast.makeText(getApplicationContext(), "Logout failed" ,
                        Toast.LENGTH_LONG).show();
            }
        }

    }
    public class Search extends AsyncTask<String, Void, String> {



        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        protected void onPostExecute(String s) {
            Intent intentq = new Intent(c, SearchActivity.class);
            intentq.putExtra("sendid",uid);
            c.startActivity(intentq);

        }

    }


}
