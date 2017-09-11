package com.example.anmol.hibiscus;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.anmol.hibiscus.Adapter.ELibraryAdapter;
import com.example.anmol.hibiscus.Model.ELibrary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Ebooksdata extends AppCompatActivity {
    TextView name,author,publisher,link,descript;
    String mname,mauth,mpublisher,mlink,mdescript,uid,pwd,dep;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    JSONObject object;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebooksdata);
        setTitle("Details");
        name = (TextView)findViewById(R.id.name);
        author = (TextView)findViewById(R.id.author);
        publisher = (TextView)findViewById(R.id.publisher);
        link = (TextView)findViewById(R.id.link);
        descript = (TextView)findViewById(R.id.desc);
        progressBar = (ProgressBar)findViewById(R.id.pg);
        object = new JSONObject();
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("hibiscus");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.child("sid").getValue()!=null && dataSnapshot.child("pwd").getValue()!=null){
                    uid = dataSnapshot.child("sid").getValue().toString();
                    pwd = dataSnapshot.child("pwd").getValue().toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.dcrypter) + pwd, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dep = response;

                            try {
                                object.put("id",getIntent().getStringExtra("id"));
                                object.put("uid",uid);
                                object.put("pwd",dep);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.ebooksdata_url), object, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressBar.setVisibility(View.GONE);
                                    try {
                                        JSONObject object = response.getJSONArray("Notices").getJSONObject(0);
                                        name.setText(object.getString("name"));
                                        author.setText(object.getString("author"));
                                        publisher.setText(object.getString("publiser"));
                                        link.setText(object.getString("link"));
                                        descript.setText(object.getString("description"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(Ebooksdata.this,"Network Error!!!",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                            Mysingleton.getInstance(Ebooksdata.this).addToRequestqueue(jsonObjectRequest);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    Mysingleton.getInstance(Ebooksdata.this).addToRequestqueue(stringRequest);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.still,R.anim.slide_out_down);
    }
}
