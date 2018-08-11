package com.anmol.hibiscus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.anmol.hibiscus.Model.Students;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button  btnLogin, btnReset;

    private static final String TAG = "Login";
    String email,sid,password;
    JSONObject object;
    String uid,pwd;
    String url = "http://139.59.23.157/api/hibi/login_test";
    private static long back_pressed;
    String crypt = "https://us-central1-iiitcloud-e9d6b.cloudfunctions.net/cryptr?pass=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setTitle("Login");
        object = new JSONObject();
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        uid = getIntent().getStringExtra("uid");
        pwd = getIntent().getStringExtra("pwd");
        String resetpass = getIntent().getStringExtra("type");
        final String usermail = getIntent().getStringExtra("email");

        // set the view now
        setContentView(R.layout.activity_login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if(resetpass.equals("resetpass")){
            final AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Reset Password")
                    .setMessage("We've sent you a password reset link at " + usermail + "@iiit-bh.ac.in. Reset the password you recently used in hibiscus and login again.")
                    .setCancelable(false)
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Resend Email", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(usermail + "@iiit-bh.ac.in").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(LoginActivity.this,"Reset Password mail sent successfully",Toast.LENGTH_LONG).show();
                                    dialogInterface.dismiss();
                                }
                            });
                        }
                    })
                    .create();
            dialog.show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sid = inputEmail.getText().toString();
                email = sid + "@iiit-bh.ac.in";
                password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Student ID!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                try {
                    object.put("uid",sid);
                    object.put("pwd",password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.login_url), object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("result").equals("success")){
                                auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                // If sign in fails, display a message to the user. If sign in succeeds
                                                // the auth state listener will be notified and logic to handle the
                                                // signed in user can be handled in the listener.
                                                if (!task.isSuccessful()) {
                                                    // there was an error
                                                    if (password.length() < 6) {
                                                        inputPassword.setError(getString(R.string.minimum_password));
                                                    } else {

                                                        auth.createUserWithEmailAndPassword(email, password)
                                                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                                                        // If sign in fails, display a message to the user. If sign in succeeds
                                                                        // the auth state listener will be notified and logic to handle the
                                                                        // signed in user can be handled in the listener.
                                                                        if (!task.isSuccessful()) {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            Toast.makeText(LoginActivity.this, "Authentication failed.Check your Network Connection",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid()).child("hibiscus");
                                                                            if(FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid())!=null) {
                                                                                FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid()).removeValue();
                                                                            }
                                                                            try {
                                                                                String encode  = URLEncoder.encode(password,"UTF-8");
                                                                                StringRequest str = new StringRequest(Request.Method.POST, crypt + encode, new Response.Listener<String>() {
                                                                                    @Override
                                                                                    public void onResponse(String response) {

                                                                                        Students students = new Students(sid,response,true);
                                                                                        ref.setValue(students).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                                String yr = String.valueOf(email.charAt(2)) + String.valueOf(email.charAt(3));
                                                                                                FirebaseMessaging.getInstance().subscribeToTopic(yr);
                                                                                                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);

                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                startActivity(intent);
                                                                                                finish();
                                                                                                overridePendingTransition(R.anim.still,R.anim.slide_in_up);
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                                Toast.makeText(LoginActivity.this,"Connection Error...Please try again!!!",Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        });

                                                                                    }
                                                                                }, new Response.ErrorListener() {
                                                                                    @Override
                                                                                    public void onErrorResponse(VolleyError error) {

                                                                                    }
                                                                                });
                                                                                Mysingleton.getInstance(LoginActivity.this).addToRequestqueue(str);

                                                                            } catch (UnsupportedEncodingException e) {
                                                                                e.printStackTrace();
                                                                            }


                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                        if(FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid())!=null) {
                                                            FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid()).removeValue();
                                                        }
                                                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid()).child("hibiscus");
                                                    try {
                                                        String encode = URLEncoder.encode(password,"UTF-8");

                                                        StringRequest str = new StringRequest(Request.Method.POST, crypt + encode, new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                Students students = new Students(sid,response,true);
                                                                ref.setValue(students).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        String yr = String.valueOf(email.charAt(2)) + String.valueOf(email.charAt(3));
                                                                        FirebaseMessaging.getInstance().subscribeToTopic(yr);

                                                                        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);

                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                        startActivity(intent);
                                                                        finish();
                                                                        overridePendingTransition(R.anim.still,R.anim.slide_in_up);

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        Toast.makeText(LoginActivity.this,"Connection Error...Please try again!!!",Toast.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                            }
                                                        }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {

                                                            }
                                                        });
                                                        Mysingleton.getInstance(LoginActivity.this).addToRequestqueue(str);
                                                    } catch (UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                    }





                                                }
                                            }
                                        });
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Please Enter Correct password",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this,"Connection Error...Please try again!!!",Toast.LENGTH_SHORT).show();

                    }
                });
                Mysingleton.getInstance(LoginActivity.this).addToRequestqueue(jsonObjectRequest);

                //authenticate user

            }


        });
    }





    @Override
    public void onBackPressed() {
        if(back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.still,R.anim.slide_out_down);
        }else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }



}

