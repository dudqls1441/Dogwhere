package com.capstone.dogwhere

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register_user_profile.*
import java.security.MessageDigest


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var db: FirebaseFirestore
    private val TAG = LoginActivity::class.java.simpleName
    private var callback: SessionCallback = SessionCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        db = FirebaseFirestore.getInstance()

        Session.getCurrentSession().addCallback(callback)

        getAppKeyHash()
        btn_join.setOnClickListener {
            startActivity(Intent(this, joinId::class.java))
        }

        btn_find_IdPW.setOnClickListener {
            startActivity(Intent(this, Finding_Id_Password::class.java))
            finish()
        }

        btn_kakao_login.setOnClickListener {
            Log.e("윤영", "들")

            Session.getCurrentSession().checkAndImplicitOpen()

        }


        btn_login.setOnClickListener {
            val userId = findViewById<EditText>(R.id.login_id).getText().toString()
            val userPwd = findViewById<EditText>(R.id.login_password).getText().toString()
            val user: User


            auth.signInWithEmailAndPassword(userId, userPwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("성공", "signInWithEmail:success")
                        val uid = auth.currentUser!!.uid
                        MySharedPreferences.setUserId(this, userId)
                        MySharedPreferences.setUserPass(this, userPwd)
                        db.collection("users").document(uid).collection("userprofiles").document(
                            uid
                        ).get()
                            .addOnSuccessListener { result ->
                                val result = result.toObject<com.capstone.dogwhere.DTO.User>()
                                if (result != null) {
                                    val intent = Intent(this, MainMenuActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(
                                        this,
                                        RegisterUserProfileActivity::class.java
                                    )
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            }
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



        if (MySharedPreferences.getUserId(this).isNullOrBlank()
            || MySharedPreferences.getUserPass(this).isNullOrBlank()
        ) {
            Log.e("1234", "자동로긘 ㄴ여기" + MySharedPreferences.getUserId(this))
//            Login()
        } else {
            auth.signInWithEmailAndPassword(
                MySharedPreferences.getUserId(this),
                MySharedPreferences.getUserPass(this)
            )
            Log.e("1234", "자동로긘 ㅇ여기" + MySharedPreferences.getUserId(this))
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    //    @SuppressLint("MissingSuperCall")
//    override fun onDestroy() {
//        Session.getCurrentSession().removeCallback(callback);
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    // 앱의 해쉬 키 얻는 함수
    private fun getAppKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                Log.e("Hash key", something)
            }
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString())
        }
    }

    private inner class SessionCallback : ISessionCallback {
        override fun onSessionOpened() {
            // 로그인 세션이 열렸을 때
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    // 로그인이 성공했을 때
                    auth.signInWithEmailAndPassword(
                        result!!.kakaoAccount.email.toString(),
                        result!!.kakaoAccount.email.toString()
                    )
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("성공", "signInWithEmail:success")
                                val uid = auth.currentUser!!.uid
                                MySharedPreferences.setUserId(
                                    this@LoginActivity,
                                    result!!.kakaoAccount.email.toString()
                                )
                                MySharedPreferences.setUserPass(
                                    this@LoginActivity,
                                    result!!.kakaoAccount.email.toString()
                                )
                                db.collection("users").document(uid).collection("userprofiles")
                                    .document(
                                        uid
                                    ).get()
                                    .addOnSuccessListener { result ->
                                        val result =
                                            result.toObject<com.capstone.dogwhere.DTO.User>()
                                        if (result != null) {
                                            val intent = Intent(
                                                this@LoginActivity,
                                                MainMenuActivity::class.java
                                            )
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        } else {
                                            val intent = Intent(
                                                this@LoginActivity,
                                                RegisterUserProfileActivity::class.java
                                            )
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                    }
                            } else {
                                // If sign in fails, display a message to the user.
                                auth.createUserWithEmailAndPassword(
                                    result!!.kakaoAccount.email.toString(),
                                    result!!.kakaoAccount.email.toString()
                                )
                                    .addOnCompleteListener(this@LoginActivity) { task ->
                                        if (task.isSuccessful) {
                                            // 가입 성공하면
                                            Log.d("성공", "signInWithEmail:success")
                                            MySharedPreferences.setUserId(
                                                this@LoginActivity,
                                                result!!.kakaoAccount.email.toString()
                                            )
                                            MySharedPreferences.setUserPass(
                                                this@LoginActivity,
                                                result!!.kakaoAccount.email.toString()
                                            )
                                            val uid = FirebaseAuth.getInstance().uid ?: ""
                                            Log.d("result", "?" + uid)
                                            val user = com.capstone.dogwhere.DTO.User(
                                                uid,
                                                result!!.kakaoAccount.email.toString(),
                                                result!!.kakaoAccount.profile.nickname.toString(),
                                                "000",
                                                false
                                            )
                                            val db =
                                                FirebaseFirestore.getInstance().collection("users")
                                            db.document(uid)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    Log.d("데이터베이스 성공", "데이터베이스:success")
                                                }
                                                .addOnFailureListener {
                                                    Log.w("데이터베이스 실패", "데이터베이스:failure")
                                                }
                                            var intent = Intent(
                                                this@LoginActivity,
                                                RegisterUserProfileActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            // 가입 실패시 , 메시지 전달
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
                override fun onSessionClosed(errorResult: ErrorResult?) {
                    // 로그인 도중 세션이 비정상적인 이유로 닫혔을 때
                    Toast.makeText(
                        this@LoginActivity,
                        "세션이 닫혔습니다. 다시 시도해주세요 : ${errorResult.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
        override fun onSessionOpenFailed(exception: KakaoException?) {
            // 로그인 세션이 정상적으로 열리지 않았을 때
            if (exception != null) {
                com.kakao.util.helper.log.Logger.e(exception)
                Toast.makeText(
                    this@LoginActivity,
                    "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요 : $exception",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun redirectSignupActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
