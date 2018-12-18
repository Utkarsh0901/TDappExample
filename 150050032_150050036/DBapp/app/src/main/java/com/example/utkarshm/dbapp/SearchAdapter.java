package com.example.utkarshm.dbapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Filter;
import android.widget.Toast;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FilterReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
//import java.util.logging.Filter;

public class SearchAdapter extends ArrayAdapter implements Filterable {
    private ArrayList<String> autocomplete = null;
    public SearchAdapter(Context context,int resource)
    {
        super(context, resource);
        autocomplete = new ArrayList<String>();
    }
    @Override
    public int getCount() {
        return autocomplete.size();
    }

    @Override
    public String getItem(int position) {
        return autocomplete.get(position);
    }
    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence c) {
                FilterResults filterResults = new FilterResults();
                if(c != null){
                        //get data from the web
                    try {
                        new Dosearch().execute(c.toString()).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }



                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(autocomplete != null && autocomplete.size() > 0){
                    Log.e("size",String.valueOf(autocomplete.size()));
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };

        return myFilter;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.searchresults,parent,false);

        //get Country
        //Country contry = mCountry.get(position);

        TextView item = (TextView) view.findViewById(R.id.searchresult);


        item.setText(autocomplete.get(position));
        return view;
    }


    public class Dosearch extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            JSONObject param = new JSONObject();
            try {
                param.put("uid",arg0[0]);
                Log.e("params",param.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("te","as1");
            }
            try {

                URL url = new URL(Url.Baseurl+"SearchUser"); // here is your URL path


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
                    Log.e("asdasdasdasdasdasd",sb.toString());
                    String result = sb.toString();
                    JSONObject jObject = null; // json
                    Log.e("asdasd",result);
                    JSONArray jArray = null;
                    //ArrayList<String> dialogitem = new ArrayList<String>();
                    String status1 = "";
                    //String data = "";
                    try {
                        jObject = new JSONObject(result);
                        //JSONObject status = jObject.getJSONObject("status"); // get data object
                        status1 = jObject.getString("status"); // get the name from data.
                        Log.e("asdasd",status1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("asdasd",status1);
                    autocomplete.clear();
                    if(status1.compareTo("true") == 0){
                        try {
                            jArray = jObject.getJSONArray("data");

                            Log.e("as","asd");
                            String[] hi = new String[] {};

                            for (int i =0 ; i<jArray.length() ; i++){
                                JSONObject some = jArray.getJSONObject(i);
                                String newUser = some.getString("uid")+" : "+some.getString("name")+" : "+some.getString("email");
                                autocomplete.add(newUser);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{

                    }
                    //Adapter.notifyAll();
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
            //autosearch.showDropDown();

            //autosearch.setAdapter(Adapter);



            //}




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