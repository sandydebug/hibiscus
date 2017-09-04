package com.example.anmol.hibiscus.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.anmol.hibiscus.Adapter.Grades;
import com.example.anmol.hibiscus.Model.Attendance;
import com.example.anmol.hibiscus.Model.Notice;
import com.example.anmol.hibiscus.Mysingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anmol on 2017-08-28.
 */

public class RequestServiceAttendance extends IntentService {
    FirebaseAuth auth;
    DatabaseReference databaseReference,noticedatabase,attendancedatabase,gradesdatabase;
    String url1 = "http://139.59.23.157/api/hibi/notice";
    String url2 = "http://139.59.23.157/api/hibi/notice_data";
    String url3 = "http://139.59.23.157/api/hibi/view_grades";
    String url4 = "http://139.59.23.157/api/hibi/attendence";
    String uid,pwd;
    int key;
    String dep;
    String decrypt = "https://us-central1-iiitcloud-e9d6b.cloudfunctions.net/dcryptr?pass=";
    String title,postedby,attention,date,id;
    public RequestServiceAttendance() {
        super("RequestService");
    }
    List<Notice> notices;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        notices = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        noticedatabase = FirebaseDatabase.getInstance().getReference().child("Notice");
        gradesdatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("grades");
        attendancedatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("attendance");
        final JSONObject jsonObject = new JSONObject();
        databaseReference.child("Students").child(auth.getCurrentUser().getUid()).child("hibiscus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.child("sid").getValue()!=null && dataSnapshot.child("pwd").getValue()!=null){
                    uid = dataSnapshot.child("sid").getValue().toString();
                    pwd = dataSnapshot.child("pwd").getValue().toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, decrypt + pwd, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dep = response;

                            try {
                                jsonObject.put("uid",uid);
                                jsonObject.put("pwd",dep);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest jsonObjectRequesta = new JsonObjectRequest(Request.Method.POST, url4, jsonObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int c = 0;
                                        while (c<response.getJSONArray("Notices").length()){

                                            JSONObject object = response.getJSONArray("Notices").getJSONObject(c);
                                            String subcode = object.getString("subcode");
                                            String sub = object.getString("sub");
                                            String name = object.getString("name");
                                            String attend = object.getString("attendance");
                                            Attendance attendance = new Attendance(subcode,sub,name,attend);
                                            attendancedatabase.child(String.valueOf(c)).setValue(attendance);
                                            c++;
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                            Mysingleton.getInstance(getApplicationContext()).addToRequestqueue(jsonObjectRequesta);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    Mysingleton.getInstance(getApplicationContext()).addToRequestqueue(stringRequest);



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
