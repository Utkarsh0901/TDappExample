package com.example.utkarshm.dbapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import org.json.*;
import android.content.Intent;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;



public class MainActivity extends AppCompatActivity {

    public static String EXTRA_MESSAGE;
    EditText id ;
    EditText pass ;
    Button loginbutton ;
    String uid ;
    String pid ;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = (EditText)findViewById(R.id.userid);
        pass = (EditText)findViewById(R.id.passid);
        loginbutton = (Button)findViewById(R.id.login);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uid =  id.getText().toString();
                pid =  pass.getText().toString();
                v = view;
                new LoginRequest().execute();

            }

        });
    }

    @Override
    public void onBackPressed(){

    }

    public class LoginRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            JSONObject param = new JSONObject();
            try {
                param.put("id",uid);
                param.put("password",pid);
                Log.e("params",param.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("te","as1");
            }

            try {

                CookieManager cookieManager =new CookieManager();

                CookieHandler.setDefault(cookieManager);

                URL url = new URL(Url.Baseurl+"Login"); // here is your URL path


                Log.e("params",param.toString());

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

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
///                    conn.setRequestProperty("Cookie",sb.toString());
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jObject  = null; // json
            String status1 = "";
            String data = "";
            try {
                jObject = new JSONObject(result);
                //JSONObject status = jObject.getJSONObject("status"); // get data object
                status1 = jObject.getString("status"); // get the name from data.
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status1.compareTo("true") == 0){

                try {
                    data = jObject.getString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                intent.putExtra("UID", data);
                startActivity(intent);
            }else{
                try {
                    data = jObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), data ,
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


}