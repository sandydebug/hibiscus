package com.anmol.hibiscus.services;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anmol.hibiscus.Model.Mycourse;
import com.anmol.hibiscus.Model.Notice;
import com.anmol.hibiscus.Mysingleton;
import com.anmol.hibiscus.R;
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

public class RequestServiceCourses extends JobIntentService {
    FirebaseAuth auth;
    DatabaseReference databaseReference,noticedatabase,attendancedatabase,gradesdatabase,coursedatabse;
    String url1 = "http://139.59.23.157/api/hibi/notice";
    String url2 = "http://139.59.23.157/api/hibi/notice_data";
    String url3 = "http://139.59.23.157/api/hibi/view_grades";
    String url4 = "http://139.59.23.157/api/hibi/attendence";
    String url5 = "http://139.59.23.157/api/hibi/mycourse";
    String uid,pwd;
    int key;
    String dep;
    String decrypt = "https://us-central1-iiitcloud-e9d6b.cloudfunctions.net/dcryptr?pass=";
    String title,postedby,attention,date,id;
    List<Notice> notices;

    public static final int JOB_ID = 3;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RequestServiceCourses.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        notices = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        noticedatabase = FirebaseDatabase.getInstance().getReference().child("Notice");
        gradesdatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("grades");
        attendancedatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("attendance");
        coursedatabse = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("mycourses");
        final JSONObject jsonObject = new JSONObject();
        databaseReference.child("Students").child(auth.getCurrentUser().getUid()).child("hibiscus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("sid").getValue(String.class)!=null && dataSnapshot.child("pwd").getValue(String.class)!=null) {
                    uid = dataSnapshot.child("sid").getValue(String.class);
                    pwd = dataSnapshot.child("pwd").getValue(String.class);
                    try {
                        jsonObject.put("uid", uid);
                        jsonObject.put("pwd", pwd);
                        jsonObject.put("pass","encrypt");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequestc = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.course_url), jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int c = 0;
                                while (c<response.getJSONArray("Notices").length()){
                                    JSONObject object = response.getJSONArray("Notices").getJSONObject(c);
                                    String name = object.getString("name");
                                    String professor = object.getString("professor");
                                    String credits = object.getString("credits");
                                    String id = object.getString("id");
                                    Mycourse mycourse = new Mycourse(name,professor,credits,id);
                                    coursedatabse.child(String.valueOf(c)).setValue(mycourse);
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
                    Mysingleton.getInstance(getApplicationContext()).addToRequestqueue(jsonObjectRequestc);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
