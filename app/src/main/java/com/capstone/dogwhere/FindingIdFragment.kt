import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.Join_User
import com.capstone.dogwhere.Finding_Id_Password
import com.capstone.dogwhere.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.finding_id_fragment.*
import java.util.*


lateinit var phone: EditText
lateinit var name: EditText
lateinit var auth: FirebaseAuth
lateinit var receiver: BroadcastReceiver


class FindingIdFragment : Fragment() {
    private val SMS_RECEIVE_PERMISSON = 1
    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 0
    private val TAG = FindingIdFragment::class.java.simpleName
    private var Check_Flag = false
    private var Check_Flag2 = false
    private var temporarily_number = ""
    private var user_name = ""
    private var user_phonenumber = ""
    private var user_id = ""

    private val numLength = 8
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
    /*  private val callbacks by lazy {
          object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
              override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                  Toast.makeText(context, "??????????????? ?????????????????????. ", Toast.LENGTH_SHORT).show()
                  Log.d(TAG, "onVerificationCompleted:$credential")
                  signInWithPhoneAuthCredential(credential)

              }

              override fun onVerificationFailed(p0: FirebaseException) {
                  TODO("Not yet implemented")
              }

              override fun onCodeSent(
                  verificationId: String,
                  token: PhoneAuthProvider.ForceResendingToken
              ) {

              }

          }

      }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.finding_id_fragment, container, false)

//        val btn_au = view.findViewById<Button>(R.id.btn_authentication_check)
        val btn_au_number = view.findViewById<Button>(R.id.btn_authentication_number)
        val btn_authentication_check = view.findViewById<Button>(R.id.btn_authentication_check)
        val btn_find_id = view.findViewById<Button>(R.id.btn_find_id)

        val activity = getActivity()


        initSmsReceiver()

        btn_au_number.setOnClickListener {
            val phone = et_phone.text.toString()
            val name = et_name.text.toString()
            check_user(name, phone)
        }
        btn_authentication_check.setOnClickListener {
            val et_temporarily_number = et_authentivation.text.toString()
            if (Check_Flag == true) {
                CheckAuthentication(et_temporarily_number, temporarily_number)
            } else {
                Toast.makeText(context, "???????????? ????????? ??????!!!", Toast.LENGTH_SHORT).show()
            }

        }
        btn_find_id.setOnClickListener {
            if (Check_Flag2 == true) {
                ShowId(user_name, user_phonenumber, user_id)
            } else {
                Toast.makeText(context, "???????????? ????????? ??????!!!", Toast.LENGTH_SHORT).show()
            }

        }
//        btn_find_pw.setOnClickListener {
//            val Factivity = activity as Finding_Id_Password
//            Factivity.setFragment(FindingPwFragment())
//
//        }





        return view
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                }
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        permission()


//        btn_authentication_number.setOnClickListener {
//            findingID()
//        }

//        btn_find_id.setOnClickListener {
//            check_user()
//        }

//        auth = FirebaseAuth.getInstance()


        /*  val options = PhoneAuthOptions.newBuilder(auth)
              .setPhoneNumber(phone.text.toString())       // Phone number to verify
              .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
              .setActivity(context as Activity)                 // Activity (for callback binding)
              .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
              .build()
          PhoneAuthProvider.verifyPhoneNumber(options)
  */


    }

//    private fun check1(username: String, phonenumber: String) {
//        Log.d(TAG, "??? ?????? : ????????? ?????? ?????? ?????? , ?????? : ${username} , ?????? : ${phonenumber}" + phonenumber)
//    }


    // 1. ????????? ?????? ????????? ??????????????? ???????????? ?????????
    private fun check_user(name: String, phone: String) {
        val db = Firebase.firestore
        Log.d(TAG, "??? ?????? : ????????? ?????? ?????? ?????? , ?????? : ${name} , ?????? : ${phone}")
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val uids = document.get("uid").toString()
                db.collection("users").document(uids).get().addOnSuccessListener { result2 ->
                    val obj = result2.toObject<Join_User>()
                    Log.d(TAG, "??????  : " + obj!!.userName)
                    if (name == obj!!.userName) {
                        Log.d(TAG, "?????? ?????? ??????, username : ${name} , ??????  : " + obj!!.userName)
                        if (phone == obj!!.userPhone) {
                            Log.d(
                                TAG,
                                "333333,????????? ?????? ?????? ?????? :?????????????????? ?????? ${obj!!.userName} ???????????? ?????? ??? ?????? ${obj!!.userPhone}"
                            )
                            user_name = obj!!.userName
                            user_phonenumber = obj!!.userPhone
                            user_id = obj!!.userId
                            Toast.makeText(
                                context,
                                "333333,????????? ?????? ?????? ?????? :?????????????????? ?????? ${obj!!.userName} ???????????? ?????? ??? ?????? ${obj!!.userPhone}",
                                Toast.LENGTH_LONG
                            ).show()

                            // ?????? ????????? ?????? ???????????? ??????
                            SendAuthentication(phone)
                            Check_Flag = true


                        } else {
                            Log.d(TAG, "4444444 ?????? ??????:${obj!!.userName}??? ???????????? ?????? ?????? ")
                        }
                    } else {
                        Log.d(TAG, "555555 ???????????? ????????? ?????? ??????.")
                    }
                }
            }
        }
    }

    //2. ???????????? ?????? ????????? ????????? ?????????
    private fun SendAuthentication(phone: String) {
        Log.d(TAG, "????????? ?????? " + phone)
        temporarily_number = excuteNumber().toString()
        val sms = "[??????] Dogwhere??? ??????????????? ${temporarily_number} ?????????"

        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, sms, null, null)
            Log.d(TAG, "000000???????????? ${temporarily_number}")
            Toast.makeText(
                context,
                "?????? ??????!",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(context, "????????? ????????? ??????", Toast.LENGTH_LONG).show()
            Log.d(TAG, e.toString())
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }


    // 3.???????????? ???????????? ?????????
    private fun CheckAuthentication(et_authentivation: String, temporarilyNumber: String) {
        if (Check_Flag == true) {
            if (et_authentivation == temporarilyNumber) {
                Log.d(
                    TAG,
                    "7777??????????????? ?????????????????????. et: ${et_authentivation} ???????????? : ${temporarilyNumber}"
                )
                Check_Flag2 = true
                Toast.makeText(context, "??????!!!!!!!!!", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "7777 ???????????? ??????. et: ${et_authentivation} ???????????? : ${temporarilyNumber}")
                Toast.makeText(context, "???????????? ??????", Toast.LENGTH_SHORT).show()

            }
        } else {
            Toast.makeText(context, "???????????? ????????? ??????!!!", Toast.LENGTH_SHORT).show()
        }

    }

    // 4. ???????????? ???????????? ???????????? ID ?????? ?????????
    private fun ShowId(user_name: String, user_phone: String, user_id: String) {
        if (Check_Flag2 == true && !user_name.equals("") && !user_phone.equals("") && !user_id.equals(
                ""
            )
        ) {
            val first_layout = view!!.findViewById<RelativeLayout>(R.id.main_RelativeLayout)
            val second_layout = view!!.findViewById<RelativeLayout>(R.id.show_RelativeLayout)
            val name = view!!.findViewById<TextView>(R.id.show_name)
            val id = view!!.findViewById<TextView>(R.id.show_id)
            Toast.makeText(
                context,
                "?????? : ${user_name} ????????? ?????? : ${user_phone} ID : ${user_id}",
                Toast.LENGTH_LONG
            ).show()
            first_layout.visibility = View.GONE
            second_layout.visibility = View.VISIBLE
            name.setText(user_name)
            id.setText(user_id)


        } else {
            Toast.makeText(context, "??????????????? ??????!", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "9999 ?????? ?????? ??? ??? ??????.")
        }

    }

    // ?????? ????????? ?????? ??????

//    private fun permission() {
//        val requirepermission = arrayOf(Manifest.permission.RECEIVE_SMS)
//        val permissioncheck =
//            ContextCompat.checkSelfPermission(context!!, Manifest.permission.RECEIVE_SMS)
//        if (permissioncheck == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "SMS ???????????? ??????", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, "SMS ???????????? ??????", Toast.LENGTH_SHORT).show()
//        }
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                activity!!,
//                Manifest.permission.RECEIVE_SMS
//            )
//        ) {
//            Toast.makeText(context, "SMS ????????? ???????????????.", Toast.LENGTH_SHORT).show()
//            ActivityCompat.requestPermissions(activity!!, requirepermission, SMS_RECEIVE_PERMISSON)
//        } else {
//            ActivityCompat.requestPermissions(activity!!, requirepermission, SMS_RECEIVE_PERMISSON)
//        }
//    }

//    private fun permission() {
//        if (ContextCompat.checkSelfPermission(
//                context!!,
//                Manifest.permission.SEND_SMS
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(
//                    activity!!,
//                    Manifest.permission.SEND_SMS
//                )
//            ) {
//                ActivityCompat.requestPermissions(
//                    activity!!,
//                    arrayOf(Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS),
//                    MY_PERMISSIONS_REQUEST_SEND_SMS
//                );
//            }
//        }
//    }

    private fun permission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,"Manifest.permission.READ_SMS") ||
            ActivityCompat.shouldShowRequestPermissionRationale(activity!!,"Manifest.permission.READ_SMS")) {
        }else{
            ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS),
                    MY_PERMISSIONS_REQUEST_SEND_SMS)

        }
    }

    private fun initSmsReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if ((intent!!.action as String) == Manifest.permission.SEND_SMS) {
                    val bundle = intent.extras

                }
            }
        }

        // ????????? ??????
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        context!!.registerReceiver(receiver, intentFilter)
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


//    private fun sendPermission() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
//        startActivityForResult(intent, SMS_CODE)
//    }

/*  private fun permission(){
      if((ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_SMS)+ ContextCompat.checkSelfPermission(context!!,Manifest.permission.SEND_SMS))!=PackageManager.PERMISSION_GRANTED){
          if(ActivityCompat.shouldShowRequestPermissionRationale(activity!!," Manifest.permission.READ_SMS")||ActivityCompat.shouldShowRequestPermissionRationale(activity!!,"Manifest.permission.SEND_SMS")){
          }else{
              ActivityCompat.requestPermissions(activity!!, Array(30,{"Manifest.permission.SEND_SMS"}),REQUEST_CODE)
          }
      }
  }*/


/*   private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }*/
