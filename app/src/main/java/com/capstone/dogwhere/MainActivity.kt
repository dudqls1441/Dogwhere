package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //test123


        auth= FirebaseAuth.getInstance()


        btn_login.setOnClickListener {
            val userId = findViewById<EditText>(R.id.login_id).getText().toString()
            val userPwd = findViewById<EditText>(R.id.login_password).getText().toString()
            auth.signInWithEmailAndPassword(userId, userPwd)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("성공", "signInWithEmail:success")

                            val intent = Intent(this, example::class.java)
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