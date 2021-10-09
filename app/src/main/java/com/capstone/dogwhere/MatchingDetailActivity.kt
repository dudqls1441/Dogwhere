package com.capstone.dogwhere


import android.content.Intent

import com.capstone.dogwhere.FCM.PushNotification
import com.capstone.dogwhere.FCM.RetrofitInstance
import com.capstone.dogwhere.FCM.NotificationAPI
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dogwhere.DTO.User
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.ImageButton
import android.widget.Toast
//import com.capstone.dogwhere.FCM.MyFirebaseMessagingService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.Matching_InUsers
import com.capstone.dogwhere.DTO.UserProfile
import com.capstone.dogwhere.DTO.Participant
import com.capstone.dogwhere.DTO.Matching
import com.capstone.dogwhere.FCM.NotificationData
import com.firepush.Fire
import com.firepush.model.PushCallback
import com.google.android.gms.auth.account.WorkAccount.API
import com.google.android.gms.common.api.Api
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import kotlinx.android.synthetic.main.activity_matching_detail.*
import kotlinx.android.synthetic.main.register_matching_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.E

class MatchingDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val adapter by lazy { PagerAdapter(supportFragmentManager, 2) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_detail)

//        val notificaiondata = NotificationData("나어디개", "매칭에 참가혔습니다.")
//        val pushnotification = PushNotification(
//            notificaiondata,
//            "cf3TmWH2SvSKk9RkOKDZLH:APA91bEqQD4nr73KpaxH7dZIzMhRBjMmEYPAWkoQjds1CgMoQDMw0RbsNNTvPu0RRJ3yg_KD8Em34_UZoiVSQjuwZWFcrQuHPtFHMVtgwl9c7wFS81vUq3JdbviUXMQixPh0fnuK38GN"
//        )
        val tokenToDevice: String =
            "cf3TmWH2SvSKk9RkOKDZLH:APA91bEqQD4nr73KpaxH7dZIzMhRBjMmEYPAWkoQjds1CgMoQDMw0RbsNNTvPu0RRJ3yg_KD8Em34_UZoiVSQjuwZWFcrQuHPtFHMVtgwl9c7wFS81vUq3JdbviUXMQixPh0fnuK38GN"
        val pushnotification: PushNotification =
            PushNotification(NotificationData("나어디개", "내 매칭에 참여자가 등록되었습니다."), tokenToDevice)
        sendNotification(pushnotification)

        Fire.init("AAAA1P59Tgs:APA91bEuZ_Hp7rsbkRmR0zWrI_uDhd9o3RMXz4oBpOeXHGc_RCJEo_-d1J-_BL5Hl4jk0KmzjZmWzzNeCOJ4n8jsiFo53QNaknXCq4fOwvbkuSpXNF08XMYud8dY8fHPl1PDMj8-_EDU\t\n")

        init()
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
            val token = task.result

            val msg = token.toString()
            Log.d("MatchingDetail", "token -> ${msg}")
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

        })
        val btn_back = findViewById<ImageButton>(R.id.btn_back)

        btn_back.setOnClickListener {
            val intent = Intent(this, MatchingListActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_chatting.setOnClickListener {
            goChatting()
        }

        btn_participate.setOnClickListener {
            participation()
        }


        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("모임설명"))
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("참여 명단"))


        matching_view_pager.adapter = MatchingPagerAdapter(supportFragmentManager, 2)
        matching_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                matching_view_pager.currentItem = tab!!.position
            }

        })
        matching_view_pager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                matching_tab_layout
            )
        )
    }


    private fun init() {
//        matchingAdded()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid.toString()

        val matchingLeaderUid = intent.getStringExtra("leaderuid").toString()
        val matchingDocumentId = intent.getStringExtra("documentId").toString()
        val matchingTitle = intent.getStringExtra("title").toString()

        db.collection("Matching").document(matchingDocumentId).collection("participant")
            .document(uid).get().addOnSuccessListener {
                Log.d("123456789", "123456 --> it -->${it.data}")
                //매칭에 참가 했을 때
                if (it.data != null) {
                    btn_participate.visibility = View.GONE
                    btn_matching_cancle.visibility = View.VISIBLE
                    db.collection("users").document(matchingLeaderUid).collection("userprofiles")
                        .document(matchingLeaderUid).get().addOnSuccessListener {
                            val result = it.toObject<UserProfile>()
                            Glide.with(this).load(result?.profilePhoto).circleCrop()
                                .into(matching_profile_img)
                            matching_profile_name.setText(result?.userName)
                        }.addOnFailureListener {
                            Log.d(
                                "MatchingDetailActivity",
                                "MatchingDetailActivity ..userprofile 가져오기 실패 : ${it}"
                            )
                        }

                    db.collection("Matching").document(matchingDocumentId).get()
                        .addOnSuccessListener {
                            val result = it.toObject<Matching>()
                            matching_title.setText(result!!.title)
                            text_matching_place.setText(result!!.place + "//" + result!!.place_detail)
                            text_matching_time.setText(result!!.startime)
                        }
                    btn_matching_cancle.setOnClickListener {
                        db.collection("Matching").document(matchingDocumentId)
                            .collection("participant")
                            .document(uid).delete()
                        db.collection("users").document(uid).collection("matching")
                            .document(matchingDocumentId).delete()
                        //새로고침
                        val dialog = CustomDialog_Cancle_Check(this)
                        dialog.mydialog()
                        dialog.setOnclickedListener(object :
                            CustomDialog_Cancle_Check.ButtonClickListener {
                            override fun onclickCancle() {
                                try {
                                    //TODO 액티비티 화면 재갱신 시키는 코드
                                    val intent = intent
                                    intent.putExtra("title", matchingTitle)
                                    intent.putExtra("documentId", matchingDocumentId)
                                    intent.putExtra("uid", uid)
                                    intent.putExtra("leaderuid", matchingLeaderUid)
                                    intent.putExtra("preActivity", "MatchingDetailActivity")
                                    finish() //현재 액티비티 종료 실시
                                    overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                                    startActivity(intent) //현재 액티비티 재실행 실시
                                    overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onlcickClose() {

                            }
                        })
                    }
                } else {
                    //Matching에 참가하지 않았을 때
                    btn_participate.visibility = View.VISIBLE
                    btn_matching_cancle.visibility = View.GONE
                    if (!uid.equals(intent.getStringExtra("leaderuid").toString())) {
                        if (intent.getStringExtra("preActivity")
                                .toString() == "MatchingListActivity"
                        ) {
                            matching_title.setText(matchingTitle)
                            db.collection("users").document(matchingLeaderUid)
                                .collection("userprofiles")
                                .document(matchingLeaderUid).get().addOnSuccessListener {
                                    val result = it.toObject<UserProfile>()
                                    Glide.with(this).load(result?.profilePhoto).circleCrop()
                                        .into(matching_profile_img)
                                    matching_profile_name.setText(result?.userName)
                                }.addOnFailureListener {
                                    Log.d(
                                        "MatchingDetailActivity",
                                        "MatchingDetailActivity ..userprofile 가져오기 실패 : ${it}"
                                    )
                                }
                            db.collection("Matching").document(matchingDocumentId).get()
                                .addOnSuccessListener {
                                    val result = it.toObject<Matching>()
                                    matching_title.setText(result!!.title)
                                    text_matching_place.setText(result!!.place + "//" + result!!.place_detail)
                                    text_matching_time.setText(result!!.startime)
                                }

                        } else if (intent.getStringExtra("preActivity")
                                .toString() == "MatchingDetailActivity"
                        ) {
                            Log.d(
                                "MatchingDetailActivity",
                                "MatchingDetailActivity : MatchingDetailActivity에서 preActivity 값 ${
                                    intent.getStringExtra(
                                        "preActivity"
                                    ).toString()
                                }"
                            )
                            matching_title.setText(matchingTitle)
                            db.collection("users").document(matchingLeaderUid)
                                .collection("userprofiles")
                                .document(matchingLeaderUid).get().addOnSuccessListener {
                                    val result = it.toObject<UserProfile>()
                                    Log.d(
                                        "MatchingDetailActivity",
                                        "MatchingDetailActivity : MatchingDetailActivity에서 왔을 때 db값 : ${result.toString()}"
                                    )
                                    Glide.with(this).load(result?.profilePhoto).circleCrop()
                                        .into(matching_profile_img)
                                    matching_profile_name.setText(result?.userName)
                                }.addOnFailureListener {
                                    Log.d(
                                        "MatchingDetailActivity",
                                        "MatchingDetailActivity ..userprofile 가져오기 실패 : ${it}"
                                    )
                                }
                            db.collection("Matching").document(matchingDocumentId).get()
                                .addOnSuccessListener {
                                    val result = it.toObject<Matching>()
                                    matching_title.setText(result!!.title)
                                    text_matching_place.setText(result!!.place + "//" + result!!.place_detail)
                                    text_matching_time.setText(result!!.startime)
                                }

                        } else if (intent.getStringExtra("preActivity")
                                .toString() == "ReservedMatchingFragment"
                        ) {
                            db.collection("Matching").document(matchingDocumentId).get()
                                .addOnSuccessListener {
                                    val result = it.toObject<Matching>()
                                    matching_title.setText(result!!.title)
                                    text_matching_place.setText(result!!.place + "//" + result!!.place_detail)
                                    text_matching_time.setText(result!!.startime)
                                    matching_title.setText(result!!.title)
                                    val matchingLeaderUid = result!!.uid
                                    db.collection("users").document(matchingLeaderUid)
                                        .collection("userprofiles")
                                        .document(matchingLeaderUid).get().addOnSuccessListener {
                                            val result = it.toObject<UserProfile>()
                                            Glide.with(this).load(result?.profilePhoto).circleCrop()
                                                .into(matching_profile_img)
                                            matching_profile_name.setText(result?.userName)

                                        }.addOnFailureListener {
                                            Log.d(
                                                "MatchingDetailActivity",
                                                "MatchingDetailActivity,,In_ReservedMatchingFragment ..userprofile 가져오기 실패 : ${it}"
                                            )
                                        }
                                }.addOnFailureListener {
                                    Log.d(
                                        "MatchingDetailActivity",
                                        "MatchingDetailActivity,,In_ReservedMatchingFragment ..userprofile 가져오기 실패 : ${it}"
                                    )
                                }
                        } else {

                        }
                        //내 매칭 들어갔을 때
                    } else {
                        btnLayout.visibility = View.GONE
                        db.collection("users").document(uid).collection("userprofiles")
                            .document(uid).get().addOnSuccessListener {
                                val result = it.toObject<UserProfile>()
                                Glide.with(this).load(result?.profilePhoto).circleCrop()
                                    .into(matching_profile_img)
                                matching_profile_name.setText(result?.userName)
                            }
                        db.collection("Matching").document(matchingDocumentId).get()
                            .addOnSuccessListener {
                                val result = it.toObject<Matching>()
                                matching_title.setText(result!!.title)
                                text_matching_place.setText(result!!.place + "//" + result!!.place_detail)
                                text_matching_time.setText(result!!.startime)
                            }
                    }
                }
            }
    }

    //채팅 메서드
    private fun goChatting() {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        if (intent.hasExtra("leaderuid")) {
            //게시물 올린 사람 uid
            val leaderUid = intent.getStringExtra("leaderuid")
            //게시물 title
            val matchingtitle = intent.getStringExtra("title").toString()
        }
    }


    //해야할 것 : 이미 참여중인 매칭에 대해서 XML처리하기


    //참여 메서드
    private fun participation() {
        var userToken = ""
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid.toString()
        db = FirebaseFirestore.getInstance()
        if (intent.hasExtra("leaderuid") && (intent.hasExtra("documentId"))) {
            val matchingLeaderUid = intent.getStringExtra("leaderuid").toString()
            val matchingtitle = intent.getStringExtra("title").toString()
            val documentId = intent.getStringExtra("documentId").toString()
            val participant_user =
                Matching_InUsers(matchingLeaderUid, uid, matchingtitle, documentId)
            val time = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
            val curTime = dateFormat.format(Date(time))

            val participant = Participant(matchingLeaderUid, uid, curTime.toString())
            Log.d("Participant", "participant_user - > ${participant_user}")

            db.collection("Matching").document(documentId).collection("participant").document(uid)
                .set(participant)
                .addOnSuccessListener {
                    Log.d("Participant", "MatchingDetailActivity_participant  성공")
                }.addOnFailureListener {
                    Log.d("Participant", "Participant 실패 이유 : ${it}")
                }

            db.collection("users").document(uid).collection("matching").document(documentId)
                .set(participant_user).addOnSuccessListener {
                    Log.d("Participant_user", "MatchingDetailActivity_participant_user  성공")

                }.addOnFailureListener {
                    Log.d("Participant", "Participant_inUser실패 이유 : ${it}")
                }

            val dialog = CustomDialog(this)
            dialog.mydialog()
            dialog.setOnclickedListener(object : CustomDialog.ButtonClickListener {
                override fun onclickMyMatchingList() {
//                    val notificaiondata = NotificationData("나어디개", "매칭에 참가혔습니다.")
//                    val pushnotification = PushNotification(
//                        notificaiondata,
//                        "APA91bEqQD4nr73KpaxH7dZIzMhRBjMmEYPAWkoQjds1CgMoQDMw0RbsNNTvPu0RRJ3yg_KD8Em34_UZoiVSQjuwZWFcrQuHPtFHMVtgwl9c7wFS81vUq3JdbviUXMQixPh0fnuK38GN"
//                    )
//                    sendNotification(pushnotification)

//                    //FCM 위한 코드
//                    //Firebase 서버키
//                    if (userToken != "cf3TmWH2SvSKk9RkOKDZLH:APA91bEqQD4nr73KpaxH7dZIzMhRBjMmEYPAWkoQjds1CgMoQDMw0RbsNNTvPu0RRJ3yg_KD8Em34_UZoiVSQjuwZWFcrQuHPtFHMVtgwl9c7wFS81vUq3JdbviUXMQixPh0fnuK38GN") {
//                        val PushNotification = PushNotification(
//                            NotificationData(matchingtitle, "매칭에 참가하였습니다."),
//                            userToken
//                        )
////                        sendNotification(PushNotification)
//                    } else {
//                        Log.d("MatchingDetail", "yb : userToken - > null임")
//                    }
//
//                    Fire.create().setTitle("나어디개").setBody("매칭에 참가하였습니다")
//                        .setCallback { pushcallback, exception ->
//                            Log.d("matchingDetail", "yb callback : ${pushcallback}")
//                            Log.d("matchingDetail", "yb exception: ${exception}")
//                        }.setBody(pushnotification.data.toString())
//                        .toIds("cf3TmWH2SvSKk9RkOKDZLH:APA91bEqQD4nr73KpaxH7dZIzMhRBjMmEYPAWkoQjds1CgMoQDMw0RbsNNTvPu0RRJ3yg_KD8Em34_UZoiVSQjuwZWFcrQuHPtFHMVtgwl9c7wFS81vUq3JdbviUXMQixPh0fnuK38GN")
//                        .push()

                    val intent =
                        Intent(this@MatchingDetailActivity, MyMatchingListActivity::class.java)
                    startActivity(intent)
                    finish()

                }

                override fun onlcickClose() {
                    try {
                        //TODO 액티비티 화면 재갱신 시키는 코드
                        val intent = intent
                        intent.putExtra("title", matchingtitle)
                        intent.putExtra("documentId", documentId)
                        intent.putExtra("uid", uid)
                        intent.putExtra("leaderuid", matchingLeaderUid)
                        intent.putExtra("preActivity", "MatchingDetailActivity")
                        finish() //현재 액티비티 종료 실시
                        overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                        startActivity(intent) //현재 액티비티 재실행 실시
                        overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            })

        }

    }

//    private fun sendNotification(notification: PushNotification) =
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = RetrofitInstance.api.sendNotification(notification)
//                if (response.isSuccessful) {
//                    Log.d("yb", "Response : ${Gson().toJson(response)}")
//                } else {
//                    Log.d("yb", "else :" + response.errorBody().toString())
//                }
//
//            } catch (e: Exception) {
//                Log.d("yb", "catch -> ${e.toString()} // ${e.message}")
//            }
//
//        }


//    private fun sendToCondition() {
//        var response_tv= ""
//        Fire.create()
//            .setTitle("실험1")
//            .setBody("sendOTcondition")
//            .setCallback { pushCallback, e ->
//                if (e == null) {
//                    response_tv= createResponseText(pushCallback)
//                    Log.d("yb","yb : respose -> ${response_tv}")
//                } else response_tv = e.message.toString()
//                Log.d("yb","yb : respose -> ${response_tv}")
//            }
//            .toCondition("target")
//            .push()
//    }
//
//    private fun createResponseText(pushCallback: PushCallback): String {
//        var text = "Response:\n\n"
//        if (pushCallback.isSent) {
//            text = text.plus("Message Sent Successfully").plus("\n\n").plus(pushCallback.jsonObject.toString())
//        } else text = text.plus("Fails to send message").plus("\n\n").plus(pushCallback.jsonObject.toString())
//        return text
//    }


    //누군가 나의 매칭에 참여했을 때 푸시 알림//
    private fun matchingAdded() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid.toString()
        val documentIdlist = mutableListOf<String>()


        val matchingLeaderUid = intent.getStringExtra("leaderuid").toString()
        val matchingDocumentId = intent.getStringExtra("documentId").toString()
        val matchingTitle = intent.getStringExtra("title").toString()

        val matching =
            FirebaseFirestore.getInstance().collection("users").document(uid).collection("matching")
                .whereEqualTo("matchingLeaderUid", uid)


        matching.get().addOnSuccessListener {
            for (document in it) {
                documentIdlist.add(document["documentId"].toString())
            }
            Log.d("matchingAdded", "matchingAdded : documentId -> ${documentIdlist}")

            for (i in documentIdlist) {
                db.collection("Matching").document(i).collection("participant")
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w("MacthingDetail", "matchingadded 메서드 snapshot 에러 : ${e.message}")
                            return@addSnapshotListener
                        }
                        if (snapshot!!.metadata.isFromCache) return@addSnapshotListener
                        for (doc in snapshot.documentChanges) {
                            //documet 에 문서가 추가되었을 때
                            if (doc.type == DocumentChange.Type.ADDED) {
                                //내 매칭에 참여한 uid
                                val addedUid = doc.document["uid"].toString()
                                val addedTime = doc.document["time"].toString()
                                Log.d("MatchingDetail", "matchingAdded - addedUid -> ${addedUid}")
                                Log.d("MatchingDetail", "matchingAdded - addedTime -> ${addedTime}")


                            }
                        }
                    }
            }


        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.sendNotification(notification)
                if (response.isSuccessful) {
                    Log.d("yb", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("yb", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("yb", e.toString())
            }
        }


}


class MatchingPagerAdapter(
    fragmentmanager: FragmentManager, val tabcount: Int
) : FragmentStatePagerAdapter(fragmentmanager) {
    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return PartyexplanationFragment()
            1 -> return ParticipantListFragment()
            else -> return PartyexplanationFragment()
        }
    }

}
