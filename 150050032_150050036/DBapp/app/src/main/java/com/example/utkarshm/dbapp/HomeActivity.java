package com.example.utkarshm.dbapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class HomeActivity extends AppCompatActivity {

    String message="";
    ListView listi;
    JSONArray jArray ;
    Context c;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listi = (ListView)findViewById(R.id.listi);
        c=this.getBaseContext();
        Intent intent = getIntent();
        message = intent.getStringExtra("UID");
        Log.e("AS", message);

        new SeePostRequest().execute();

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
                //new AddPost().execute();
                Intent intent = new Intent(c, AddPostActivity.class);
                intent.putExtra("UID", message);
                startActivity(intent);
                return true;
            case R.id.mlogout:
                new Logout().execute();;
                return true;
            case R.id.mseepost:
                new SeePostRequest().execute();;
                return true;
            case R.id.msearch:
                new Search().execute();;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            intentq.putExtra("sendid",message);
            c.startActivity(intentq);

        }

    }
    public class SeePostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            try {


                URL url = new URL(Url.Baseurl+"SeePosts"); // here is your URL path

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
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
                    jArray = jObject.getJSONArray("data");

                    Log.e("as","asd");
                    String[] hi = new String[] {};
                    ArrayList<User> arrayOfUsers = new ArrayList<User>();
                    UsersAdapter arrayAdapter = new UsersAdapter(HomeActivity.this, arrayOfUsers);

                    for (int i =0 ; i<jArray.length() ; i++){
                        JSONObject some = jArray.getJSONObject(i);
                        if(some.getString("uid").compareTo(message) == 0){
                            continue;
                        }
                        User newUser = new User(jArray.getJSONObject(i).toString());
                        arrayAdapter.add(newUser);
                    }
                    listi.setAdapter(arrayAdapter);
                    Log.e("Aasddd","asdasd");
                    listi.setOnScrollListener(new EndlessScrollListener() {
                        @Override
                        public boolean onLoadMore(int page, int totalItemsCount) {

                            return false; // ONLY if more data is actually being loaded; false otherwise.
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    data = jObject.getString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), data ,
                        Toast.LENGTH_LONG).show();
            }
        }


    }
    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        if(requestCode == 1){
            new SeePostRequest().execute();
        }
    }
}

