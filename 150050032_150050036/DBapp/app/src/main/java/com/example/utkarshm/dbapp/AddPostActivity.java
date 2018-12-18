package com.example.utkarshm.dbapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class AddPostActivity extends AppCompatActivity {

    Button postbutton;
    Button uploadbutton;
    ImageView image;
    String imagestring= ":image:";
    String poststring ="";
    EditText post ;
    String UID = "";
    private static final int SELECT_PICTURE = 2200;
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Intent intent = getIntent();
        UID =intent.getStringExtra("UID");
        c=this.getBaseContext();
        postbutton = (Button)findViewById(R.id.addpost_button);
        uploadbutton = (Button)findViewById(R.id.image_button);
        image = (ImageView) findViewById(R.id.uimageView);
        post = (EditText)findViewById(R.id.writepost);
        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }

        });
        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poststring = post.getText().toString()+imagestring;
                if(poststring.compareTo(":image:") == 0){
                    Intent intent = new Intent(c, HomeActivity.class);
                    intent.putExtra("UID",UID);
                    startActivity(intent);
                }else{
                    new Dopost().execute(poststring);
                }

            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPath(selectedImageUri);
                    Log.e("tab", "Image Path : " + path);
                    // Set the image in ImageView
                    image.setImageURI(selectedImageUri);
                    Bitmap bitmap =null;
                    try {
                         bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                    //bitmap.
                    byte[] ba = bao.toByteArray();
                    imagestring = imagestring + Base64.encodeToString(ba,Base64.DEFAULT);
                }
            }
        }
    }
    public String getPath(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    public class Dopost extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            JSONObject param = new JSONObject();
            try {
                param.put("content",arg0[0]);
                Log.e("params",param.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("te","as1");
            }

            try {

                Log.e("aa",Url.Baseurl);
                URL url = new URL(Url.Baseurl+ "CreatePost"); // here is your URL path


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
            //String data = "";
            try {
                jObject = new JSONObject(result);
                //JSONObject status = jObject.getJSONObject("status"); // get data object
                status1 = jObject.getString("status"); // get the name from data.
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status1.compareTo("true") == 0){
                Log.e("Status",UID);
                Intent intent = new Intent(c, HomeActivity.class);
                intent.putExtra("UID",UID);
                startActivity(intent);
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


