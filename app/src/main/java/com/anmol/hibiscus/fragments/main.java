package com.anmol.hibiscus.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anmol.hibiscus.Adapter.IcoAdapter;
import com.anmol.hibiscus.Adapter.NoticeAdapter;
import com.anmol.hibiscus.Helpers.Dbhelper;
import com.anmol.hibiscus.Interfaces.ItemClickListener;
import com.anmol.hibiscus.Model.Notice;
import com.anmol.hibiscus.Mysingleton;
import com.anmol.hibiscus.R;
import com.anmol.hibiscus.WebviewActivity;
import com.anmol.hibiscus.services.RequestService;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;

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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anmol on 2017-08-18.
 */

public class main extends Fragment {
    String url = "http://139.59.23.157/api/hibi/notice";
    SpinKitView progressBar;
    String title,date,postedby,id,attention;
    List<Notice> notices = new ArrayList<>();
    NoticeAdapter adapter;
    ListView lv;
    RecyclerView rv;
    int key;
    FirebaseAuth auth;
    String uid,pwd;
    DatabaseReference mdatabase;
    Animation rotate;
    Button retry;
    String dep;
    String decrypt = "https://us-central1-iiitcloud-e9d6b.cloudfunctions.net/dcryptr?pass=";
    TextView head,body;
    Button work;
    ImageView back;
    View margin;
    SwipeRefreshLayout noticerefresh;
    ItemClickListener itemClickListener;
    IcoAdapter icoAdapter;
    CircleImageView showstar;
    Boolean starred;
    Dbhelper dbhelper;
    ArrayList<String> noticeids;
    Button searchbtn,cancelbtn;
    EditText searchedit;
    LinearLayout myView;
    List<Notice> searchednotices;
    Boolean isLoading = false;
    List <Notice> loadnotices = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.main,container,false);
        getActivity().setTitle("Notice Board");
        Intent intent = new Intent(getActivity(), RequestService.class);
        getActivity().startService(intent);
        searchednotices =  new ArrayList<>();
        showstar = (CircleImageView)vi.findViewById(R.id.showstar);
        myView = vi.findViewById(R.id.searchlayout);
        searchbtn = vi.findViewById(R.id.searchbtn);
        cancelbtn = vi.findViewById(R.id.cancelbtn);
        searchedit = vi.findViewById(R.id.searchedit);
        lv = (ListView)vi.findViewById(R.id.list);
        rv = (RecyclerView)vi.findViewById(R.id.rv);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        final ImageButton refresh = (ImageButton)vi.findViewById(R.id.refresh);
        retry = (Button)vi.findViewById(R.id.retry);
        head = (TextView)vi.findViewById(R.id.head);
        body = (TextView)vi.findViewById(R.id.body);
        work = (Button)vi.findViewById(R.id.work);
        margin = (View)vi.findViewById(R.id.margin);
        rotate = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
        auth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Notice");
        progressBar = (SpinKitView) vi.findViewById(R.id.load);
        progressBar.setVisibility(View.VISIBLE);
        final DatabaseReference data = FirebaseDatabase.getInstance().getReference().getRoot().child("banner");
        dbhelper = new Dbhelper(getActivity());
        noticerefresh = (SwipeRefreshLayout)vi.findViewById(R.id.noticerefresh);
//        refresh.setOnClickListener(newfeature View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = newfeature Intent(getActivity(), RequestService.class);
//                getActivity().startService(intent);
//                Toast.makeText(getActivity(),"Please Wait...",Toast.LENGTH_SHORT).show();
//                refresh.startAnimation(rotate);
//            }
//        });
        itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }
        };
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoading = true;
                searchedit.setVisibility(View.VISIBLE);
                searchedit.requestFocus();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchedit, 0);
                searchbtn.setVisibility(View.GONE);
                cancelbtn.setVisibility(View.VISIBLE);
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedit.setVisibility(View.INVISIBLE);
                searchedit.clearFocus();
                searchedit.setText(null);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getView().getWindowToken(), 0);
                cancelbtn.setVisibility(View.GONE);
                searchbtn.setVisibility(View.VISIBLE);
            }
        });
        starred = false;
        Glide.with(getActivity()).load(R.drawable.starunfillwhite).into(showstar);
        icoAdapter = new IcoAdapter(getActivity(),loadnotices,itemClickListener);
        rv.setAdapter(icoAdapter);
        loadnotice(0,40,true);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    int visibleItemcount = layoutManager.getChildCount();
                    int totalItemcount = layoutManager.getItemCount();
                    int pastVisibleitems = layoutManager.findFirstVisibleItemPosition();
                    if(visibleItemcount + pastVisibleitems >= totalItemcount && !isLoading){
                        progressBar.setVisibility(View.VISIBLE);
                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadnotice(40,0,false);
                            }
                        },2500);
                        isLoading = true;
                    }
                }
            }
        });
        showstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!starred){
                    starred = true;
                    Glide.with(getActivity()).load(R.drawable.stargolden).into(showstar);
                    loadbook();
                    isLoading = true;
                }
                else{
                    starred = false;
                    Glide.with(getActivity()).load(R.drawable.starunfillwhite).into(showstar);
                    loadnotice(0,40,true);
                    isLoading = false;
                }
            }
        });






        noticerefresh.setColorSchemeColors(
                getActivity().getResources().getColor(R.color.colorAccent)
        );
        noticerefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String query = "Select * from notice_table ORDER BY notice_id DESC";
                List<Notice> allnotices = dbhelper.readData(query);
                noticeids = new ArrayList<>();
                for(int i = 0;i<allnotices.size();i++){
                    noticeids.add(allnotices.get(i).getId());
                }
                auth = FirebaseAuth.getInstance();
                final DatabaseReference databaseReference,noticedatabase,attendancedatabase,gradesdatabase;
                databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
                noticedatabase = FirebaseDatabase.getInstance().getReference().child("Notice");
                gradesdatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("grades");
                attendancedatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(auth.getCurrentUser().getUid()).child("attendance");
                final JSONObject jsonObject = new JSONObject();
                databaseReference.child("Students").child(auth.getCurrentUser().getUid()).child("hibiscus").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("sid").getValue(String.class)!=null && dataSnapshot.child("pwd").getValue(String.class)!=null){
                            uid = dataSnapshot.child("sid").getValue(String.class);
                            pwd = dataSnapshot.child("pwd").getValue(String.class);
                            try {
                                jsonObject.put("uid",uid);
                                jsonObject.put("pwd",pwd);
                                jsonObject.put("pass","encrypt");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println("noticeresponse null");
                            try{
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.notice_url), jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        noticerefresh.setRefreshing(false);
                                        try {

                                            int c = 0;
                                            while (c<5){
                                                System.out.println("noticeresponse" + response);
                                                JSONObject object = response.getJSONArray("Notices").getJSONObject(c);


                                                title = object.getString("title");
                                                date = object.getString("date");
                                                postedby = object.getString("posted_by");
                                                attention = object.getString("attention");
                                                id = object.getString("id");
                                                Notice notice = new Notice(title,date,postedby,attention,id,false,false);
                                                int k=0;
                                                for(int j = 0;j<noticeids.size();j++){
                                                    if(noticeids.get(j).equals(id)){
                                                        k=1;
                                                    }
                                                }
                                                if(k==0){
                                                    System.out.print("noticestatus:newfeature entry");
                                                    dbhelper.insertData(notice);
                                                }
                                                else{
                                                    System.out.print("noticestatus:already present");
                                                }
                                                //dbhelper.insertData(notice);
                                                dbhelper.updatenotice(notice);
                                                //noticedatabase.child(String.valueOf(c)).setValue(notice);
                                                c++;
                                            }
                                            loadnotice(0,40,true);
                                            isLoading = false;
//                                            notices.clear();
//                                            loadnotices.clear();
//                                            String query = "Select * from notice_table ORDER BY notice_id DESC";
//                                            notices = dbhelper.readData(query);
//                                            loadnotices.addAll(notices);
//                                            if (!notices.isEmpty()){
//                                                if(getActivity()!=null){
//                                                    progressBar.setVisibility(View.GONE);
//                                                    //icoAdapter = new IcoAdapter(getActivity(),notices,itemClickListener);
//                                                    icoAdapter.notifyDataSetChanged();
//                                                    //rv.setAdapter(icoAdapter);
//                                                }
//
//
//                                            }
//                                            else {
//                                                mdatabase.addValueEventListener(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                        notices.clear();
//                                                        loadnotices.clear();
//                                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                                                            String title = data.child("title").getValue().toString();
//                                                            String attention = data.child("attention").getValue().toString();
//                                                            String posted_by = data.child("posted_by").getValue().toString();
//                                                            String date = data.child("date").getValue().toString();
//                                                            String id = data.child("id").getValue().toString();
//                                                            Notice notice = new Notice(title, date, posted_by, attention, id,false,false);
//                                                            loadnotices.add(notice);
//
//
//                                                        }
//                                                        if (getActivity() != null) {
//                                                            progressBar.setVisibility(View.GONE);
////                                                            adapter = newfeature NoticeAdapter(getActivity(), R.layout.notice, notices);
////                                                            adapter.notifyDataSetChanged();
////                                                            lv.setAdapter(adapter);
//                                                            //icoAdapter = new IcoAdapter(getActivity(),notices,itemClickListener);
//                                                            icoAdapter.notifyDataSetChanged();
//                                                            //rv.setAdapter(icoAdapter);
//
//                                                        }
//
//
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                            }
                                            if(getActivity()!=null){
                                                Toast.makeText(getActivity(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        catch (NullPointerException e){
                                            e.printStackTrace();
                                        }
//                                        try {
//                                            JSONObject object0 = response.getJSONArray("Notices").getJSONObject(0);
//
//                                            title = object0.getString("title");
//                                            date = object0.getString("date");
//                                            postedby = object0.getString("posted_by");
//                                            attention = object0.getString("attention");
//                                            id = object0.getString("id");
//                                            Notice notice = newfeature Notice(title,date,postedby,attention,id);
//                                            FirebaseDatabase.getInstance().getReference().child("Notices").setValue(notice);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        noticerefresh.setRefreshing(false);
                                        if(getActivity()!=null){
                                            Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                if(getActivity()!=null){
                                    Mysingleton.getInstance(getActivity()).addToRequestqueue(jsonObjectRequest);
                                }

                            }
                            catch (IllegalStateException e){
                                e.printStackTrace();
                            }



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dhead = dataSnapshot.child("head").getValue(String.class);
                String dbody = dataSnapshot.child("body").getValue(String.class);
                String dname = dataSnapshot.child("button").child("name").getValue(String.class);
                final String dlink = dataSnapshot.child("button").child("link").getValue(String.class);
                final Boolean webview = dataSnapshot.child("button").child("webview").getValue(Boolean.class);

                if(dhead!=null && !dhead.isEmpty()){
                    head.setText(dhead);
                    head.setVisibility(View.VISIBLE);

                }
                else {
                    head.setVisibility(View.GONE);
                }
                if(dbody!=null && !dbody.isEmpty()){
                    body.setText(dbody);
                    body.setVisibility(View.VISIBLE);

                }
                else {
                    body.setVisibility(View.GONE);
                }
                if(dname!=null && !dname.isEmpty()){
                    work.setText(dname);
                    work.setVisibility(View.VISIBLE);

                }
                else {
                    work.setVisibility(View.GONE);
                }
                if((dhead==null || dhead.isEmpty())&&(dbody == null || dbody.isEmpty())&&(dname==null || dname.isEmpty())){
                    margin.setVisibility(View.GONE);
                }
                else {
                    margin.setVisibility(View.VISIBLE);
                }
                work.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(dlink!=null){
                            if(webview){
                                Intent webintent = new Intent(getActivity(),WebviewActivity.class);
                                webintent.putExtra("weburl",dlink);
                                startActivity(webintent);
                            }
                            else {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dlink));
                                startActivity(browserIntent);
                            }

                        }

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return vi;


    }

    private void loadbook() {
        notices.clear();
        loadnotices.clear();
        String querybook = "Select * from notice_table WHERE bookmark="+1+" ORDER BY notice_id DESC";

        notices = dbhelper.readData(querybook);
        loadnotices.addAll(notices);
        if (!notices.isEmpty()){
            if(getActivity()!=null){
                progressBar.setVisibility(View.GONE);

                //icoAdapter = new IcoAdapter(getActivity(),notices,itemClickListener);
                icoAdapter.notifyDataSetChanged();
                //rv.setAdapter(icoAdapter);
            }

            searchedit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    loadnotices.clear();

                        for(int j=0;j<notices.size();j++){
                            if(notices.get(j).getTitle().toLowerCase().contains(charSequence) ||
                                    notices.get(j).getTitle().toUpperCase().contains(charSequence) ||
                                    notices.get(j).getAttention().toLowerCase().contains(charSequence) ||
                                    notices.get(j).getAttention().toUpperCase().contains(charSequence) ||
                                    notices.get(j).getPosted_by().toLowerCase().contains(charSequence) ||
                                    notices.get(j).getTitle().contains(charSequence) ||
                                    notices.get(j).getAttention().contains(charSequence) ||
                                    notices.get(j).getPosted_by().contains(charSequence) ||
                                    notices.get(j).getPosted_by().toUpperCase().contains(charSequence)
                                    ){
                                loadnotices.add(notices.get(j));
                            }
                        }
                        if(getActivity()!=null){
                            progressBar.setVisibility(View.GONE);
                            //icoAdapter = new IcoAdapter(getActivity(),searchednotices,itemClickListener);
                            icoAdapter.notifyDataSetChanged();
                            //rv.setAdapter(icoAdapter);
                        }

                    }


                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        else {
            if(getActivity()!=null){
                Toast.makeText(getActivity(),"You don't have any starred notices yet",Toast.LENGTH_SHORT).show();
                loadnotice(0,40,true);
                starred = false;
                isLoading = false;
                Glide.with(getActivity()).load(R.drawable.starunfillwhite).into(showstar);

            }

        }

    }

    private void loadnotice(int offset,int numofnotices,Boolean clear) {
        notices.clear();
        if(clear){
            loadnotices.clear();
        }
        String query;
        if(numofnotices == 0){
            query = "Select * from notice_table ORDER BY notice_id DESC LIMIT -1 OFFSET " + offset;
        }
        else
        {
            query = "Select * from notice_table ORDER BY notice_id DESC LIMIT " + numofnotices;
        }

        notices = dbhelper.readData(query);
        loadnotices.addAll(notices);
        //notices.addAll(dbhelper.readData(query));
        searchedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadnotices.clear();
                if(!notices.isEmpty()){
                    for(int j=0;j<notices.size();j++){
                        if(notices.get(j).getTitle().toLowerCase().contains(charSequence) ||
                                notices.get(j).getTitle().toUpperCase().contains(charSequence) ||
                                notices.get(j).getAttention().toLowerCase().contains(charSequence) ||
                                notices.get(j).getAttention().toUpperCase().contains(charSequence) ||
                                notices.get(j).getPosted_by().toLowerCase().contains(charSequence) ||
                                notices.get(j).getTitle().contains(charSequence) ||
                                notices.get(j).getAttention().contains(charSequence) ||
                                notices.get(j).getPosted_by().contains(charSequence) ||
                                notices.get(j).getPosted_by().toUpperCase().contains(charSequence)
                                ){
                                loadnotices.add(notices.get(j));
                        }
                    }
                    if(getActivity()!=null){
                        progressBar.setVisibility(View.GONE);
                        //icoAdapter = new IcoAdapter(getActivity(),searchednotices,itemClickListener);
                        icoAdapter.notifyDataSetChanged();
                        //rv.setAdapter(icoAdapter);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (!notices.isEmpty()){
            System.out.println("important:" + notices.size());
            if(getActivity()!=null){
                progressBar.setVisibility(View.GONE);
                //icoAdapter = new IcoAdapter(getActivity(),notices,itemClickListener);
                icoAdapter.notifyDataSetChanged();
//                rv.setAdapter(icoAdapter);
            }


        }
        else {
            mdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notices.clear();
                    loadnotices.clear();
                    for(DataSnapshot data:dataSnapshot.getChildren()){

                        String title = data.child("title").getValue().toString();
                        String attention = data.child("attention").getValue().toString();
                        String posted_by = data.child("posted_by").getValue().toString();
                        String date = data.child("date").getValue().toString();
                        String id = data.child("id").getValue().toString();
                        Notice notice = new Notice(title,date,posted_by,attention,id,false,false);
                        loadnotices.add(notice);


                    }
                    if(getActivity()!=null){
                        progressBar.setVisibility(View.GONE);
//                        adapter = newfeature NoticeAdapter(getActivity(),R.layout.notice,notices);
//                        adapter.notifyDataSetChanged();
//                        lv.setAdapter(adapter);
                        //icoAdapter = new IcoAdapter(getActivity(),notices,itemClickListener);
                        icoAdapter.notifyDataSetChanged();
                        //rv.setAdapter(icoAdapter);

                    }
                    searchedit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            loadnotices.clear();
                            if(!notices.isEmpty()){
                                for(int j=0;j<notices.size();j++){
                                    if(notices.get(j).getTitle().toLowerCase().contains(charSequence) ||
                                            notices.get(j).getTitle().toUpperCase().contains(charSequence) ||
                                            notices.get(j).getAttention().toLowerCase().contains(charSequence) ||
                                            notices.get(j).getAttention().toUpperCase().contains(charSequence) ||
                                            notices.get(j).getPosted_by().toLowerCase().contains(charSequence) ||
                                            notices.get(j).getTitle().contains(charSequence) ||
                                            notices.get(j).getAttention().contains(charSequence) ||
                                            notices.get(j).getPosted_by().contains(charSequence) ||
                                            notices.get(j).getPosted_by().toUpperCase().contains(charSequence)
                                            ){
                                        loadnotices.add(notices.get(j));
                                    }
                                }
                                if(getActivity()!=null){
                                    progressBar.setVisibility(View.GONE);
                                    //icoAdapter = new IcoAdapter(getActivity(),searchednotices,itemClickListener);
                                    icoAdapter.notifyDataSetChanged();
                                    //rv.setAdapter(icoAdapter);

                                }
                                                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        //isLoading = false;

    }

}
