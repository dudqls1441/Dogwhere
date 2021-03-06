package com.capstone.dogwhere

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dogwhere.DTO.DogProfile
import com.capstone.dogwhere.DTO.UserProfile
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
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
            finish()
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
        }
        line.setOnClickListener {
            startActivity(Intent(this, joinId::class.java))
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
        }

        btn_find_IdPW.setOnClickListener {
            startActivity(Intent(this, Finding_Id_Password::class.java))
            finish()
        }

        btn_kakao_login.setOnClickListener {
            Log.e("??????", "???")

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
                        Log.d("??????", "signInWithEmail:success")
                        val uid = auth.currentUser!!.uid
                        MySharedPreferences.setUserId(this, userId)
                        MySharedPreferences.setUserPass(this, userPwd)
                        db.collection("users").document(uid).collection("dogprofiles").document(uid)
                            .get()
                            .addOnSuccessListener { result1 ->
                                val result = result1.toObject<DogProfile>()
                                Log.d("ybyb","dogprofile->${result1.exists()}")
                                if (result != null) {
                                    val intent = Intent(this, MainMenuActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                } else {
                                    db.collection("users").document(uid).collection("userprofiles")
                                        .document(uid).get()
                                        .addOnSuccessListener { result2 ->
                                            val result = result2.toObject<UserProfile>()
                                            Log.d("ybyb","userprofile->${result2.exists()}")
                                            if (result != null) {
                                                val intent =
                                                    Intent(this, DogProfileActivity::class.java)
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
                                }
                            }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("??????", "signInWithEmail:failure", task.exception)
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
            Log.e("1234", "???????????? ?????????" + MySharedPreferences.getUserId(this))
//            Login()
        } else {
            auth.signInWithEmailAndPassword(
                MySharedPreferences.getUserId(this),
                MySharedPreferences.getUserPass(this)
            )
            Log.e("1234", "???????????? ?????????" + MySharedPreferences.getUserId(this))
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

    // ?????? ?????? ??? ?????? ??????
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
            // ????????? ????????? ????????? ???
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    // ???????????? ???????????? ???
                    try{
                        if(!result!!.kakaoAccount.phoneNumber.equals(null)){
                            Log.d("ybyb","phone ->${result!!.kakaoAccount.phoneNumber}")
                        }
                    }catch (e: Exception){
                        Log.d("ybyb","kakao phone recieve ->${e.toString()}")
                    }

                    auth.signInWithEmailAndPassword(
                        result!!.kakaoAccount.email.toString(),
                        result!!.kakaoAccount.email.toString()
                    )
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("??????", "signInWithEmail:success")
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
                                            // ?????? ????????????
                                            Log.d("??????", "signInWithEmail:success")
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
                                                "01062933317", getToken(),
                                                false
                                            )
                                            val db =
                                                FirebaseFirestore.getInstance().collection("users")
                                            db.document(uid)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    Log.d("?????????????????? ??????", "??????????????????:success")
                                                }
                                                .addOnFailureListener {
                                                    Log.w("?????????????????? ??????", "??????????????????:failure")
                                                }
                                            var intent = Intent(
                                                this@LoginActivity,
                                                RegisterUserProfileActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            // ?????? ????????? , ????????? ??????
                                            Log.w("??????", "signInWithEmail:failure", task.exception)
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
                    // ????????? ?????? ????????? ??????????????? ????????? ????????? ???
                    Toast.makeText(
                        this@LoginActivity,
                        "????????? ???????????????. ?????? ?????????????????? : ${errorResult.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            // ????????? ????????? ??????????????? ????????? ????????? ???
            if (exception != null) {
                com.kakao.util.helper.log.Logger.e(exception)
                Toast.makeText(
                    this@LoginActivity,
                    "????????? ?????? ????????? ??????????????????. ????????? ????????? ?????????????????? : $exception",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun getToken(): String {
        var token = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    "MatchingDetail",
                    "MatchingDetail Fetching FCM registration token failed ",
                    task.exception
                )
                return@OnCompleteListener
            }

            //Get new FCM registration token
            token = task.result

            val msg = token.toString()
            Log.d("MatchingDetail", "token -> ${msg}")
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

        })

        return token
    }

    private fun redirectSignupActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
