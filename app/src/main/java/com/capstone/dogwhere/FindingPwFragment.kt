import android.content.Intent
import com.capstone.dogwhere.R

import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.Join_User
import com.capstone.dogwhere.MainMenuActivity
import com.capstone.dogwhere.MySharedPreferences
import com.capstone.dogwhere.RegisterUserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.finding_id_fragment.*
import kotlinx.android.synthetic.main.finding_id_fragment.et_name
import kotlinx.android.synthetic.main.finding_id_fragment.et_phone
import kotlinx.android.synthetic.main.finding_pw_fragment.*
import java.util.*


class FindingPwFragment : Fragment() {

    private var Check_Flag2: Boolean = false
    private var Check_Flag: Boolean = false
    private val TAG = FindingPwFragment::class.java.simpleName
    private var temporarily_number = ""
    private var numLength: Int = 8
    private var user_name = ""
    private var user_phonenumber = ""
    private var user_id = ""
    private val charTable = charArrayOf(
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        '0'
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.finding_pw_fragment, container, false)

        val btn_au_number = view.findViewById<Button>(R.id.btn_authentication_number)
        val btn_authentication_check = view.findViewById<Button>(R.id.btn_authentication_check)
        val btn_find_pw = view.findViewById<Button>(R.id.btn_find_pw)

        btn_au_number.setOnClickListener {
            val id = et_id.text.toString()
            val name = et_name.text.toString()
            val phone = et_phone.text.toString()

            check_user(id,name,phone)
        }

        btn_authentication_check.setOnClickListener {
            val et_temporarily_number = et_pw_authentivation.text.toString()
            if (Check_Flag == true) {
                CheckAuthentication(et_temporarily_number, temporarily_number)
            } else {
                Toast.makeText(context, "인증번호를 요청하시오.", Toast.LENGTH_SHORT).show()
            }
        }
        btn_find_pw.setOnClickListener {
            ChangePW(user_name,user_phonenumber,user_id)
        }


        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    private fun check_user(id: String, name: String, phone: String) {
        val db = Firebase.firestore
        Log.d(TAG, "첫 번째 : 기입된 번호 이름 확인 , 이름 : ${name} , 번호 : ${phone}")
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val uids = document.get("uid").toString()
                db.collection("users").document(uids).get().addOnSuccessListener { result2 ->
                    val obj = result2.toObject<Join_User>()
                    if (id == obj!!.userId) {
//                        Log.d(TAG, "해당 이름 있음, username : ${id} , 이름  : " + obj!!.userId)
                        if (name == obj!!.userName) {
                            //           Log.d(TAG, "해당 이름 있음, username : ${name} , 이름  : " + obj!!.userName)
                            if (phone == obj!!.userPhone) {
                                Log.d(
                                    TAG,
                                    "해당 이름 있음,id : ${obj!!.userId} username :${obj!!.userName} , userPhone:  ${phone} ,등록되어있는 폰 번호 : " + obj!!.userPhone
                                )
                                user_id = obj!!.userId
                                user_name= obj!!.userName
                                user_phonenumber=obj!!.userPhone
                                SendAuthentication(phone)
                                Check_Flag = true
                            } else {
                            }
                        } else {
                        }
                    } else {
                    }
                }
            }
        }
    }

    private fun SendAuthentication(phone: String) {
        Log.d(TAG, "핸드폰 번호 " + phone)
        val sms = "문자 메세지 내용"+temporarily_number
        temporarily_number = excuteNumber().toString()

        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, sms, null, null)
            Log.d(TAG, "1234임시번호  : ${temporarily_number}")
            Toast.makeText(
                context,
                "전송 완료!",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(context, "메시지 보내기 실패", Toast.LENGTH_LONG).show()
            Log.d(TAG, e.toString())
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun CheckAuthentication(et_authentivation: String, temporarilyNumber: String) {
        if (Check_Flag == true) {
            if (et_authentivation == temporarilyNumber) {
                Log.d(
                    TAG,
                    "7777인증번호가 확인되었습니다. et: ${et_authentivation} 인증번호 : ${temporarilyNumber}"
                )
                Check_Flag2 = true
                Toast.makeText(context, "통과!!!!!!!!!", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "7777 인증번호 틀림. et: ${et_authentivation} 인증번호 : ${temporarilyNumber}")
                Toast.makeText(context, "인증번호 틀림", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "인증번호 요청을 하라!!!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun ChangePW(user_name: String, user_phone: String, user_id: String) {
        if (Check_Flag2 == true && !user_name.equals("") && !user_phone.equals("") && !user_id.equals("")) {

            val auth = FirebaseAuth.getInstance()
            val email ="dudqls1441@naver.com"
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                Log.d(TAG,"이메일 전송 성공")
            }.addOnFailureListener {
                Log.d(TAG,"이메일 전송 실패")
            }
            val user = FirebaseAuth.getInstance()

//            auth.signInWithEmailAndPassword(userId, userPwd)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d("성공", "signInWithEmail:success")
//                        val uid = auth.currentUser!!.uid
//                        MySharedPreferences.setUserId(this, userId)
//                        MySharedPreferences.setUserPass(this, userPwd)
//                        db.collection("users").document(uid).collection("userprofiles").document(
//                            uid
//                        ).get()
//                            .addOnSuccessListener { result ->
//                                val result = result.toObject<com.capstone.dogwhere.DTO.User>()
//                                if (result != null) {
//                                    val intent = Intent(this, MainMenuActivity::class.java)
//                                    intent.flags =
//                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    startActivity(intent)
//                                } else {
//                                    val intent = Intent(
//                                        this,
//                                        RegisterUserProfileActivity::class.java
//                                    )
//                                    intent.flags =
//                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    startActivity(intent)
//                                }
//                            }
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w("실패", "signInWithEmail:failure", task.exception)
//                        Toast.makeText(
//                            baseContext, "Authentication failed.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                }
//        }


        }
    }


    fun excuteNumber(): String? {
        val random = Random(System.currentTimeMillis())
        val tablelength = charTable.size
        val buf = StringBuffer()
        for (i in 0 until numLength) {
            buf.append(charTable[random.nextInt(tablelength)])
        }
        return buf.toString()
    }
}





