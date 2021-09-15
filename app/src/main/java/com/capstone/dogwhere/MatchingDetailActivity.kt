package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.Matching_InUsers
import com.capstone.dogwhere.DTO.UserProfile
import com.capstone.dogwhere.DTO.Participant
import com.capstone.dogwhere.DTO.Matching
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_matching_detail.*
import kotlinx.android.synthetic.main.register_matching_dialog.*
import java.text.SimpleDateFormat
import java.util.*

class MatchingDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val adapter by lazy { PagerAdapter(supportFragmentManager, 2) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_detail)

        init()
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
                        dialog.setOnclickedListener(object : CustomDialog_Cancle_Check.ButtonClickListener{
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


//    private fun init() {
//        auth = FirebaseAuth.getInstance()
//        db = FirebaseFirestore.getInstance()
//        val uid = auth.currentUser?.uid.toString()
//
//        val matchingLeaderUid = intent.getStringExtra("leaderuid").toString()
//        val matchingDocumentId = intent.getStringExtra("documentId").toString()
//        val matchingTitle = intent.getStringExtra("title").toString()
//
//
//        if (!uid.equals(intent.getStringExtra("leaderuid").toString())) {
//            if (intent.getStringExtra("preActivity").toString() == "MatchingListActivity") {
//                matching_title.setText(matchingTitle)
//                db.collection("users").document(matchingLeaderUid).collection("userprofiles")
//                    .document(matchingLeaderUid).get().addOnSuccessListener {
//                        val result = it.toObject<UserProfile>()
//                        Glide.with(this).load(result?.profilePhoto).circleCrop()
//                            .into(matching_profile_img)
//                        matching_profile_name.setText(result?.userName)
//                    }.addOnFailureListener {
//                        Log.d(
//                            "MatchingDetailActivity",
//                            "MatchingDetailActivity ..userprofile 가져오기 실패 : ${it}"
//                        )
//                    }
//                db.collection("Matching").document(matchingDocumentId).get().addOnSuccessListener {
//                    val result = it.toObject<Matching>()
//                    matching_title.setText(result!!.title)
//                    text_matching_place.setText(result!!.place + "//" + result!!.place_detail)
//                    text_matching_time.setText(result!!.startime)
//                }
//
//            } else if (intent.getStringExtra("preActivity")
//                    .toString() == "MatchingDetailActivity"
//            ) {
//                Log.d(
//                    "MatchingDetailActivity",
//                    "MatchingDetailActivity : MatchingDetailActivity에서 preActivity 값 ${
//                        intent.getStringExtra(
//                            "preActivity"
//                        ).toString()
//                    }"
//                )
//                matching_title.setText(matchingTitle)
//                db.collection("users").document(matchingLeaderUid).collection("userprofiles")
//                    .document(matchingLeaderUid).get().addOnSuccessListener {
//                        val result = it.toObject<UserProfile>()
//                        Log.d(
//                            "MatchingDetailActivity",
//                            "MatchingDetailActivity : MatchingDetailActivity에서 왔을 때 db값 : ${result.toString()}"
//                        )
//                        Glide.with(this).load(result?.profilePhoto).circleCrop()
//                            .into(matching_profile_img)
//                        matching_profile_name.setText(result?.userName)
//                    }.addOnFailureListener {
//                        Log.d(
//                            "MatchingDetailActivity",
//                            "MatchingDetailActivity ..userprofile 가져오기 실패 : ${it}"
//                        )
//                    }
//                db.collection("Matching").document(matchingDocumentId).get().addOnSuccessListener {
//                    val result = it.toObject<Matching>()
//                    matching_title.setText(result!!.title)
//                    text_matching_place.setText(result!!.place + "//" + result!!.place_detail)
//                    text_matching_time.setText(result!!.startime)
//                }
//
//            } else {
//
//            }
//            //내 매칭 들어갔을 때
//        } else {
//            btnLayout.visibility = View.GONE
//            db.collection("users").document(uid).collection("userprofiles")
//                .document(uid).get().addOnSuccessListener {
//                    val result = it.toObject<UserProfile>()
//                    Glide.with(this).load(result?.profilePhoto).circleCrop()
//                        .into(matching_profile_img)
//                    matching_profile_name.setText(result?.userName)
//                }
//            db.collection("Matching").document(matchingDocumentId).get().addOnSuccessListener {
//                val result = it.toObject<Matching>()
//                matching_title.setText(result!!.title)
//                text_matching_place.setText(result!!.place + "//" + result!!.place_detail)
//                text_matching_time.setText(result!!.startime)
//            }
//
//        }
//    }

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
                    val intent =Intent(this@MatchingDetailActivity,MyMatchingListActivity::class.java)
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
