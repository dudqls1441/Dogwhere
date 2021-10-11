package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dogwhere.DTO.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_join_id.*
import kotlinx.coroutines.*

class joinId : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_id)



        auth = FirebaseAuth.getInstance()

        btn_back.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btn_join_in_Join.setOnClickListener {

            val join_userid = findViewById<EditText>(R.id.join_id_et).getText().toString()
            val join_userPassword =
                findViewById<EditText>(R.id.join_password_et).getText().toString()
            val join_userPassword2 =
                findViewById<EditText>(R.id.join_passwordC_ed).getText().toString()
            val join_userPhone = findViewById<EditText>(R.id.join_phone_et).getText().toString()
            val join_userName = findViewById<EditText>(R.id.join_name_et).getText().toString()
            if (join_userPassword == join_userPassword2) {
                if (join_userPassword.length >= 6) {
                    auth.createUserWithEmailAndPassword(join_userid, join_userPassword)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // 가입 성공하면
                                Log.d("성공", "signInWithEmail:success")


                                val uid = FirebaseAuth.getInstance().uid ?: ""

                                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                    OnCompleteListener { task ->
                                        if (!task.isSuccessful) {
                                            Log.w(
                                                "MatchingDetail",
                                                "MatchingDetail Fetching FCM registration token failed ",
                                                task.exception
                                            )
                                            return@OnCompleteListener
                                        }

                                        //Get new FCM registration token
                                        val token = task.result

                                        val msg = token.toString()
                                        Log.d("join", "In Joinactivity,, token -> ${msg}")
                                        val user =
                                            User(
                                                uid,
                                                join_userid,
                                                join_userName,
                                                join_userPhone,
                                                msg,
                                                false
                                            )

                                        val db = FirebaseFirestore.getInstance().collection("users")
                                        db.document(uid)
                                            .set(user)
                                            .addOnSuccessListener {
                                                Log.d("데이터베이스 성공", "데이터베이스:success")
                                                Log.d("yb", "yb : user -> ${user}")
                                            }
                                            .addOnFailureListener {
                                                Log.w("데이터베이스 실패", "데이터베이스:failure")
                                            }
                                        val intent = Intent(this, LoginActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        overridePendingTransition(R.anim.slide_up_enter,R.anim.slide_up_eixt)
                                    })
                            } else {
                                try {
                                    task.getResult()
                                } catch (e: Exception) {
                                    // 가입 실패시 , 메시지 전달
                                    Log.w("실패", "이메일을 확인해주세요", task.exception)
                                    Toast.makeText(
                                        baseContext, "이메일을 확인해주세요",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                } else {
                    Toast.makeText(this, "6자리 이상 비밀번호를 사용해주세요", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
