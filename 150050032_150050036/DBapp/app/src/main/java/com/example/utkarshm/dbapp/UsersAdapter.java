package com.example.utkarshm.dbapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<User> {
    Context tempcontext ;
    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        tempcontext = context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.see_post, parent, false);
        }
        // Lookup view for data population
        final View tempview = convertView;
        final TextView seepost = (TextView) convertView.findViewById(R.id.seepost);
        TextView time = (TextView) convertView.findViewById(R.id.timestamp);
        ListView li = (ListView) convertView.findViewById(R.id.comments);
        ImageView image = (ImageView)convertView.findViewById(R.id.imageView) ;
        Button add_comment = (Button)convertView.findViewById(R.id.add_comment);
        final Button more_comments = (Button)convertView.findViewById(R.id.more_comments);


        ArrayList<String> arrayOfUsers = new ArrayList<String>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(convertView.getContext(),android.R.layout.simple_list_item_1, arrayOfUsers);
        // Populate the data into the template view using the data object

        JSONObject jObject  = null;
        JSONObject commentObject = null;

        String timestamp = "";
        JSONArray commentArray1 = null;
        String newUser = "";
        String cname = "";
        String ctext = "";
        String ctimestamp = "";
        String postid = "";
        String text = "";
        String Uid = "";
        String[] iarray;
        Bitmap bitmap;
        byte[] ba = {};
        try {
            jObject = new JSONObject(user.postdata);
            text = jObject.getString("text");
            Uid = jObject.getString("uid");
            timestamp = "- " + jObject.getString("timestamp");
            commentArray1= jObject.getJSONArray("Comment");
            postid = jObject.getString("postid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(text.contains(":image:")){
            iarray = text.split(":image:");
            text = iarray[0];
            if(iarray.length!=1){
                ba = Base64.decode(iarray[1],Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(ba,0,ba.length);
                image.setImageBitmap(bitmap);
            }
        }else{
            bitmap = BitmapFactory.decodeByteArray(ba,0,ba.length);
            image.setImageBitmap(bitmap);
        }



        final String postid1 = postid;
        final JSONArray commentArray = commentArray1;
        final Paint textpaint = seepost.getPaint();
        Rect bound = new Rect();
        int a=0;

        seepost.setText(Uid +"\n" + text);
        time.setText(timestamp);
        if(commentArray.length() ==0){
            newUser = new String("No Comments");
            textpaint.getTextBounds(newUser,0,newUser.length(),bound);
            a = a+bound.height()+40;
            arrayAdapter.add(newUser);
        }
        int len =0;
        if(commentArray.length()<=3){
            len = commentArray.length();
        }else{
            len = 3;
            more_comments.setVisibility(View.VISIBLE);
        }

        for (int i =0 ; i<len ; i++){
            try {

                commentObject = commentArray.getJSONObject(i);
                cname = new String(commentObject.getString("name"));
                textpaint.getTextBounds(cname,0,cname.length(),bound);
                a = a+bound.height()+40;
                ctext = new String(commentObject.getString("text"));
                textpaint.getTextBounds(ctext,0,ctext.length(),bound);
                a = a+bound.height();
                ctimestamp = new String("                   - "+commentObject.getString("timestamp"));
                textpaint.getTextBounds(ctimestamp,0,ctimestamp.length(),bound);
                a = a+bound.height();

                Log.e("ASD",ctimestamp);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            arrayAdapter.add(cname  + "\n" + ctext +"\n" + ctimestamp);
            //arrayAdapter.add(ctext);
            //arrayAdapter.add(ctimestamp);
        }
        LayoutParams list = (LayoutParams) li.getLayoutParams();
        Log.e("asd",String.valueOf(a));
        list.height = a;
        li.setLayoutParams(list);
        li.setAdapter(arrayAdapter);

        final ListView lis = (ListView) convertView.findViewById(R.id.comments);
        final ArrayList<String> arrayOfUsers1 = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(convertView.getContext(),android.R.layout.simple_list_item_1,arrayOfUsers1);





        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentq = new Intent(tempcontext, CommentActivity.class);
                intentq.putExtra("activity", postid1);
                intentq.putExtra("activity", postid1);
                ((Activity) tempcontext).startActivityForResult(intentq,1);
                //tempcontext.startActivityForResult();
            }


        });




        more_comments.setOnClickListener(new View.OnClickListener() {

            JSONObject commentObject = null;
            String cname = "";
            String ctext = "";
            String ctimestamp = "";

            Rect bound = new Rect();
            int a=0;


            @Override
            public void onClick(View view) {
                for (int i =0 ; i<commentArray.length() ; i++){
                    try {

                        commentObject = commentArray.getJSONObject(i);
                        cname = new String(commentObject.getString("name"));
                        textpaint.getTextBounds(cname,0,cname.length(),bound);
                        a = a+bound.height()+40;
                        ctext = new String(commentObject.getString("text"));
                        textpaint.getTextBounds(ctext,0,ctext.length(),bound);
                        a = a+bound.height();
                        ctimestamp = new String("                   - "+commentObject.getString("timestamp"));
                        textpaint.getTextBounds(ctimestamp,0,ctimestamp.length(),bound);
                        a = a+bound.height();

                        Log.e("ASD",ctimestamp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    arrayAdapter1.add(cname  + "\n" + ctext +"\n" + ctimestamp);
                    //arrayAdapter.add(ctext);
                    //arrayAdapter.add(ctimestamp);
                }
                more_comments.setVisibility(View.INVISIBLE);
                LayoutParams list = (LayoutParams) lis.getLayoutParams();
                Log.e("asd",String.valueOf(a));
                list.height = a;
                lis.setLayoutParams(list);
                lis.setAdapter(arrayAdapter1);

            }

        });

        //TextView comment = new TextView(convertView.getContext());
        //Return the completed view to render on screen
        return convertView;
    }
}
