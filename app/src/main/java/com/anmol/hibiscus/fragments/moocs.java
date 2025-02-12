package com.anmol.hibiscus.fragments;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anmol.hibiscus.Adapter.MoocsAdapter;
import com.anmol.hibiscus.Model.Moocs;
import com.anmol.hibiscus.Mysingleton;
import com.anmol.hibiscus.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anmol on 9/14/2017.
 */

public class moocs extends Fragment {
    ListView lv;
    String uid,pwd,name,link;
    ProgressBar progressBar;
    List<Moocs>moocses = new ArrayList<>();
    MoocsAdapter moocsAdapter;
    JSONObject jsonObject =  new JSONObject();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Button retry;
    ImageView fail;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("hibiscus");
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.moocs,container,false);
        getActivity().setTitle("Moocs");

        retry = (Button)vi.findViewById(R.id.retry);
        lv = (ListView)vi.findViewById(R.id.list);
        progressBar = (ProgressBar)vi.findViewById(R.id.load);
        progressBar.setVisibility(View.VISIBLE);
        fail = (ImageView)vi.findViewById(R.id.fail);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.moocs_url), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    int c = 0;
                    moocses.clear();
                    while (c<response.getJSONArray("Notices").length()){

                        JSONObject object = response.getJSONArray("Notices").getJSONObject(c);


                        name = object.getString("name");
                        link = object.getString("link");
                        Moocs moocs = new Moocs(name,link);
                        moocses.add(moocs);

                        c++;
                    }
                    if(getActivity()!=null){
                        moocsAdapter = new MoocsAdapter(getActivity(),R.layout.moocslist,moocses);
                        moocsAdapter.notifyDataSetChanged();
                        if(!moocsAdapter.isEmpty()){
                            lv.setAdapter(moocsAdapter);
                            progressBar.setVisibility(View.GONE);
                            retry.setVisibility(View.GONE);
                            fail.setVisibility(View.GONE);
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            retry.setVisibility(View.VISIBLE);
                            fail.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(getActivity()!=null && isAdded()){
                    retry.setVisibility(View.VISIBLE);
                    fail.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
                }

            }
        });
        Mysingleton.getInstance(getActivity()).addToRequestqueue(jsonObjectRequest);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry.setVisibility(View.GONE);
                fail.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.moocs_url), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int c = 0;
                            moocses.clear();
                            while (c<response.getJSONArray("Notices").length()){

                                JSONObject object = response.getJSONArray("Notices").getJSONObject(c);


                                name = object.getString("name");
                                link = object.getString("link");
                                Moocs moocs = new Moocs(name,link);
                                moocses.add(moocs);

                                c++;
                            }
                            if(getActivity()!=null){
                                moocsAdapter = new MoocsAdapter(getActivity(),R.layout.moocslist,moocses);
                                moocsAdapter.notifyDataSetChanged();
                                if(!moocsAdapter.isEmpty()){
                                    lv.setAdapter(moocsAdapter);
                                    progressBar.setVisibility(View.GONE);
                                    retry.setVisibility(View.GONE);
                                    fail.setVisibility(View.GONE);
                                }
                                else {
                                    progressBar.setVisibility(View.GONE);
                                    retry.setVisibility(View.VISIBLE);
                                    fail.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(getActivity()!=null && isAdded()){
                            retry.setVisibility(View.VISIBLE);
                            fail.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                Mysingleton.getInstance(getActivity()).addToRequestqueue(jsonObjectRequest);
            }
        });
        return vi;
    }
}
