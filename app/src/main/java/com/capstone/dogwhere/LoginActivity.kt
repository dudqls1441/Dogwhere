package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join_id.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var db : FirebaseFirestore
    private val TAG =LoginActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //test123


        auth= FirebaseAuth.getInstance()
        database = Firebase.database.reference
        db = FirebaseFirestore.getInstance()

        btn_going_to_join.setOnClickListener {
            startActivity(Intent(this,joinId::class.java))
        }


        btn_login.setOnClickListener {
            val userId = findViewById<EditText>(R.id.login_id).getText().toString()
            val userPwd = findViewById<EditText>(R.id.login_password).getText().toString()

            val user :User


            auth.signInWithEmailAndPassword(userId, userPwd)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("성공", "signInWithEmail:success")
                            var registeredUserProfile :Boolean
                            db.collection("user")
                                    .get().addOnSuccessListener { result->
                                        for (document  in result){
                                            registeredUserProfile = document.get("registeredUserProfile") as Boolean
                                            Log.d("읽기",document.get("registeredUserProfile").toString())
                                            Log.d("읽기", registeredUserProfile.toString())
                                            Log.d("데이터베이스읽기성공","${document.id}=>${document.data}")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w("데이터베이스읽기실패","Error getting document",exception)
                                    }


                            val intent = Intent(this, RegisterUserProfileActivity::class.java)
                            intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("실패", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
        }
    }
}