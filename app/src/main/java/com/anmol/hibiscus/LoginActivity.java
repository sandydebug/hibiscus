package com.anmol.hibiscus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.anmol.hibiscus.Model.Students;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;


    String email,sid,password;
    JSONObject object;
    String uid,pwd;

    private static long back_pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
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

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnReset = (Button) findViewById(R.id.btn_reset_password);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        if(resetpass!=null){
            if(resetpass.equals("resetpass")){
                String titleText = "Reset Password";

                // Initialize a newfeature foreground color span instance
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent));

                // Initialize a newfeature spannable string builder instance
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

                // Apply the text color span

                ssBuilder.setSpan(
                        foregroundColorSpan,
                        0,
                        titleText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                    final AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(ssBuilder)
                            .setMessage("We've sent you a password reset link at " + usermail + "@iiit-bh.ac.in . Reset Canopy password with your new Hibiscus password and login again.")
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
                                            Toast.makeText(LoginActivity.this,"Password reset mail sent successfully",Toast.LENGTH_LONG).show();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                }
                            })
                            .create();
                    dialog.show();


//                FirebaseDatabase.getInstance().getReference().child("passwordreset").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

            }
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(LoginActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(10,5,10,5);
                input.setLayoutParams(lp);
                input.setHint("Enter your student id:");
                String titleText = "Changed hibiscus Password?";

                // Initialize a newfeature foreground color span instance
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent));

                // Initialize a newfeature spannable string builder instance
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

                // Apply the text color span

                ssBuilder.setSpan(
                        foregroundColorSpan,
                        0,
                        titleText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(ssBuilder)
                            .setMessage("Canopy will not work properly if you've changed your hibiscus password. You need to reset the same for canopy also.")
                            .setCancelable(true)
                            .setView(input)
                            .setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    String inputid = input.getText().toString();
                                    if(inputid.length() != 7){
                                        Toast.makeText(LoginActivity.this,"Please enter a valid Student id",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(inputid.toLowerCase() + "@iiit-bh.ac.in").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(LoginActivity.this,"Reset Password mail sent successfully",Toast.LENGTH_LONG).show();
                                                dialogInterface.dismiss();
                                            }
                                        });
                                    }


                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();


                    dialog.show();



            }
        });
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
                                                                            FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid()).removeValue();
                                                                            Students students = new Students(sid,password,true);
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
                                                                                    e.printStackTrace();
                                                                                    Toast.makeText(LoginActivity.this,"Connection Error...Please try again!!!",Toast.LENGTH_LONG).show();
                                                                                }
                                                                            });


                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid()).removeValue();
                                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Students").child(auth.getCurrentUser().getUid()).child("hibiscus");
                                                    Students students = new Students(sid,password,true);
                                                    ref.setValue(students).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            String yr = String.valueOf(email.charAt(2)) + String.valueOf(email.charAt(3));
                                                            FirebaseMessaging.getInstance().subscribeToTopic(yr);

                                                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                                            Toast.makeText(LoginActivity.this,"Login Successful ",Toast.LENGTH_SHORT).show();

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
                                                            e.printStackTrace();
                                                            Toast.makeText(LoginActivity.this,"Connection Error...Please try again!!!",Toast.LENGTH_LONG).show();
                                                        }
                                                    });





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
                        error.printStackTrace();
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

