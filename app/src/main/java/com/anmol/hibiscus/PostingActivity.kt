package com.anmol.hibiscus

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_posting.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PostingActivity : AppCompatActivity() {
    var auth:FirebaseAuth?=null
    var hibdatabase:DatabaseReference?=null
    var studentdatabase:DatabaseReference?=null
    var studentdatabaseid:DatabaseReference?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.colorAccent)
        }
        setContentView(R.layout.activity_posting)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        title = "Post Notice"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        auth = FirebaseAuth.getInstance()
        studentdatabase = FirebaseDatabase.getInstance().reference.child("Studentnoticeboard")
        studentdatabaseid = FirebaseDatabase.getInstance().reference.child("Studentnoticeid")
        hibdatabase = FirebaseDatabase.getInstance().reference.child("Students").child(auth!!.currentUser!!.uid).child("hibiscus")
        submitnotice.setOnClickListener{
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy hh:mm a")
            val formattedDate = df.format(c)
            val noticetitlestring = noticetitle!!.text.toString()
            val noticedescription = noticedes!!.text.toString()
            if(!noticetitlestring.isEmpty() && !noticedescription.isEmpty()){
                hibdatabase!!.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val uid = p0.child("sid").value.toString()
                        studentdatabaseid!!.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val size:Long = p0.child("Studentnoticeid").value as Long
                                val map = HashMap<String,Any>()
                                map["title"] = noticetitlestring
                                map["description"] = noticedescription
                                map["postedby"] = uid
                                map["time"] = formattedDate
                                studentdatabase!!.child((size + 1).toString()).setValue(map).addOnCompleteListener {
                                    val map2 = HashMap<String,Any>()
                                    map2["Studentnoticeid"] = size + 1
                                    studentdatabaseid!!.updateChildren(map2)
                                }

                            }

                        })


                    }

                })
            }
            else{
                Toast.makeText(this,"fields cannot be empty",Toast.LENGTH_SHORT).show()
            }
        }

    }
}
