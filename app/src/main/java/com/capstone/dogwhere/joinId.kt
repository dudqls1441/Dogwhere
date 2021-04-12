package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_join_id.*

class joinId : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_id)

        auth= FirebaseAuth.getInstance()
        btn_join_in_Join.setOnClickListener {
            val join_userid=findViewById<EditText>(R.id.join_id_et).getText().toString()
            val join_userPassword=findViewById<EditText>(R.id.join_password_et).getText().toString()
            val join_userPassword2=findViewById<EditText>(R.id.join_passwordC_ed).getText().toString()
            val join_userPhone=findViewById<EditText>(R.id.join_phone_et).getText().toString()
            val join_userName=findViewById<EditText>(R.id.join_name_et).getText().toString()
            val join_userAddress=findViewById<EditText>(R.id.join_address_et).getText().toString()

            if(join_userPassword==join_userPassword2) {
                auth.createUserWithEmailAndPassword(join_userid, join_userPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 가입 성공하면 
                            Log.d("성공", "signInWithEmail:success")

                            val uid = FirebaseAuth.getInstance().uid?:""
                            val user = User(uid,join_userid,join_userName,join_userPhone,join_userAddress)

                            val db = FirebaseFirestore.getInstance().collection("users")
                            db.document(uid)
                                .set(user)
                                .addOnSuccessListener {
                                    Log.d("데이터베이스 성공", "데이터베이스:success")
                                }
                                .addOnFailureListener {
                                    Log.w("데이터베이스 실패", "데이터베이스:failure")
                                }
                            val intent = Intent(this,MainActivity::class.java)
                            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        } else {
                            // 가입 실패시 , 메시지 전달
                            Log.w("실패", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        // ...

                    }
            }else{
                Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}