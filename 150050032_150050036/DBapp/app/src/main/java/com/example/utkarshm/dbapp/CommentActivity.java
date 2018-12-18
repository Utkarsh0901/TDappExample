package com.example.utkarshm.dbapp;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class CommentActivity extends AppCompatActivity {

    String message = "";
    EditText comment = null;
    Button cbutton = null;
    String data = "";
    View v = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        //String activity = intent.getStringExtra("activity");
       // Log.e("as",activity);
        message = intent.getStringExtra("activity");
        Log.e("as",message);

        comment = (EditText) findViewById(R.id.write_comment);
        cbutton = (Button) findViewById(R.id.comment_button);

        cbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data =  comment.getText().toString();
                v = view;
                new AddComment().execute();

            }

        });

    }

    public class AddComment extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            JSONObject param = new JSONObject();
            try {
                param.put("postid",message);
                param.put("content",data);
                Log.e("params",param.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("te","as1");
            }

            try {

                URL url = new URL(Url.Baseurl+"NewComment"); // here is your URL path


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
            try {
                jObject = new JSONObject(result);
                //JSONObject status = jObject.getJSONObject("status"); // get data object
                status1 = jObject.getString("status"); // get the name from data.
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status1.compareTo("true") == 0){
                finish();
                //Intent intent = new Intent(v.getContext(), HomeActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, data);
                //startActivity(intent);


            }else{

                Toast.makeText(getApplicationContext(), "error" ,
                        Toast.LENGTH_LONG).show();
            }




        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
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
