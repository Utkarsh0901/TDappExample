package com.example.utkarshm.dbapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.StringTokenizer;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SearchActivity extends AppCompatActivity {

    AutoCompleteTextView autosearch;
    ArrayList<String> options = new ArrayList<String>();
    SearchAdapter Adapter ;
    String message ="";
    String searchstring ="";
    int i=0;
    Context c ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        autosearch = (AutoCompleteTextView)findViewById(R.id.auto_complete);
        autosearch.setInputType(InputType.TYPE_CLASS_TEXT);
        c=this.getBaseContext();
        //options.add("something");
        Intent i = getIntent();
        message = i.getStringExtra("sendid");
        //autosearch.fil
        Adapter = new SearchAdapter(SearchActivity.this,1);
        autosearch.setAdapter(Adapter);
        //autosearch.setThreshold(1);

        autosearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("utkarsh",autosearch.getText().toString());
                String me =autosearch.getText().toString();
                String[] check = me.split(" : ");
                Log.e(check[0],message);
                if(check[0].compareTo(message)==0)
                {
                    Log.e(check[0],message);

                    Intent intent = new Intent(c, FollowShowPostActivity.class);
                    intent.putExtra("FOLLOWUID",me);
                    intent.putExtra("UID", message);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(c, FollowActivity.class);
                    intent.putExtra("FOLLOW", me);
                    intent.putExtra("UID", message);
                    startActivity(intent);
                }
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
                intent1.putExtra("UID", message);
                startActivity(intent1);
                return true;
            case R.id.mlogout:
                new SearchActivity.Logout().execute();
                return true;
            case R.id.mseepost:
                Intent intent = new Intent(c, HomeActivity.class);
                intent.putExtra("UID",message);
                startActivity(intent);
                return true;
            case R.id.msearch:
                new SearchActivity.Search().execute();
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

}
