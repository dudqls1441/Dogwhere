package com.capstone.dogwhere


//import com.capstone.dogwhere.FCM.MyFirebaseMessagingService
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.*
import com.capstone.dogwhere.GroupChat.*
import com.capstone.dogwhere.FCM.MyReceiver
import com.firepush.Fire
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_check.*
import kotlinx.android.synthetic.main.activity_matching_detail.*
import kotlinx.android.synthetic.main.activity_matching_detail.btn_back
import kotlinx.android.synthetic.main.activity_matching_registration.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val TOPIC = "/topics/myTopic"

class MatchingDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var dogname = ArrayList<String>()
    var map: GoogleMap? = null
    var mLM: LocationManager? = null
    var mProvider = LocationManager.NETWORK_PROVIDER
    var mylocation: LatLng? = null
    var matching_dog_neu = ""
    var matching_dog_size = ""
    var matching_owner_gender = ""
    var name = ""
    private lateinit var matchingLeaderUid: String
    private lateinit var matchingDocumentId: String
    private lateinit var matchingTitle: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_detail)
        Fire.init("AAAA1P59Tgs:APA91bEuZ_Hp7rsbkRmR0zWrI_uDhd9o3RMXz4oBpOeXHGc_RCJEo_-d1J-_BL5Hl4jk0KmzjZmWzzNeCOJ4n8jsiFo53QNaknXCq4fOwvbkuSpXNF08XMYud8dY8fHPl1PDMj8-_EDU")
        matchingLeaderUid = intent.getStringExtra("leaderuid").toString()
        matchingDocumentId = intent.getStringExtra("documentId").toString()
        matchingTitle = intent.getStringExtra("title").toString()

        mLM = getSystemService(LOCATION_SERVICE) as LocationManager
        val fragment = supportFragmentManager
            .findFragmentById(R.id.matchingdetail_mapfragment) as SupportMapFragment?
        fragment!!.getMapAsync(this)


        // matchingAdded()
        init()



        btn_group_chat.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()
            val uid = auth.currentUser?.uid.toString()
            val intent = Intent(this, GroupChatRoomActivity::class.java)
            db.collection("users").document(uid.toString())
                .collection("userprofiles").document(uid.toString())
                .get()
                .addOnSuccessListener {
                    name = it.get("userName").toString()
                    Log.d("ybyb", "yourUid:" + uid + "  name:" + name)
                    intent.putExtra("yourUid", uid)
                    intent.putExtra("matchingDocumentId", matchingDocumentId)
                    intent.putExtra("name", name)
                    intent.putExtra("matchingTitle", matchingTitle)
                    startActivity(intent)
                    finish()
                }

        }

        btn_Done.setOnClickListener {
            DoneDialog()
        }
        btn_back.setOnClickListener {
            finish()
        }
        btn_trash.setOnClickListener {
            deleteDialog()
        }
        btn_chatting.setOnClickListener {
            goChatting()
        }

        btn_participate.setOnClickListener {
            Intent(this, MatchingRegistration_Choice_Dog_Activity::class.java).apply {
                putExtra("dogchoice_state", "matching_participant")
            }.run { startActivityForResult(this, 110) }
        }
        matching_profile_img.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("uid", matchingLeaderUid)
            startActivity(intent)
        }


        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("????????????"))
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("?????? ????????? ??????"))


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

        matchingLeaderUid = intent.getStringExtra("leaderuid").toString()
        matchingTitle = intent.getStringExtra("title").toString()


        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yy/MM/dd")
        val curTime = dateFormat.format(Date(time))

        val timeFormat = SimpleDateFormat("HH/mm")
        val curtime2 = timeFormat.format(Date(time))

        val now_hour = curtime2.split("/")[0]
        val now_minute = curtime2.split("/")[1]

        val now_time_minute = now_hour.toInt() * 60 + now_minute.toInt()

        //?????? ?????? ????????? ???
        if (uid == matchingLeaderUid) {
            btn_trash.visibility = View.VISIBLE
            btn_participate.visibility = View.GONE
            btnLayout.visibility = View.GONE
            btn_group_chat.visibility = View.VISIBLE

            val matchinglist = mutableListOf<String>()
            db.collection("users").document(uid).collection("matching").get().addOnSuccessListener {
                for (document in it) {
                    if (uid.equals(document["matchingLeaderUid"].toString())) {
                        matchinglist.add(document["documentId"].toString())
                    }
                }
                Log.d("ybyb", "atchingList -> ${matchinglist}")
                if (!matchinglist.isEmpty()) {
                    //.whereEqualTo("ongoing",false)
                    db.collection("Matching").whereIn("documentId", matchinglist)
                        .whereEqualTo("ongoing", true).get()
                        .addOnSuccessListener {
                            for (document in it) {
                                Log.d("ybyb", "RegisterdMatching = ${document.id}")
                            }
                        }
                }
            }


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
                    text_matching_place.setText("?????? :" + result!!.place + " " + result!!.place_detail)

                    val ongoing = result!!.ongoing
                    Log.d("ybyb", "ongoing -> ${ongoing}")

                    if (ongoing.equals(false)) {
                        btn_Done.visibility = View.GONE
                    }

                    val date = result!!.date
                    val splitedDate = date.split("/")

                    //?????? ??????
                    val splitedStartTime = result!!.startime.split("/")
                    val start_time_minute =
                        splitedStartTime[0].toInt() * 60 + splitedStartTime[1].toInt()

                    //?????? ??????
                    val splitedDoneTime = result!!.doneTime.split("/")
                    val Done_time_minute =
                        splitedDoneTime[0].toInt() * 60 + splitedDoneTime[1].toInt()


                    if (curTime.equals(date)) {
                        if (ongoing.equals(true)) {
                            if (now_time_minute < start_time_minute) {
                                val countDownTimer = object : CountDownTimer(200000, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {
                                        text_matching_time_today.visibility = View.VISIBLE
                                        btn_Done.visibility = View.GONE
                                        text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                        text_matching_time_today.setText(
                                            "??????" +
                                                    getTime(
                                                        splitedDate[1].toInt(),
                                                        splitedDate[2].toInt(),
                                                        splitedStartTime[0].toInt(),
                                                        splitedStartTime[1].toInt()
                                                    )
                                        )
                                    }

                                    override fun onFinish() {}
                                }
                                countDownTimer.start()
                            } else if (now_time_minute >= start_time_minute && now_time_minute <= Done_time_minute) {
                                text_matching_time_today.visibility = View.VISIBLE
                                text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                text_matching_time_today.setText("?????? ???????????? ???????????????")
                                btn_Done.visibility = View.VISIBLE
                                Writer_profile.setBackgroundResource(R.drawable.round_mint)
                                text_matching_time_today.setTextColor(R.color.main_mint.toInt())
                            } else {
                                text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                text_matching_time_today.setText("?????? ????????? ???????????????.")
                                btn_Done.visibility = View.VISIBLE
                            }
                        } else {
                            text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                            text_matching_time_today.visibility = View.VISIBLE
                            text_matching_time_today.setText("?????? ????????? ???????????????.")
                        }
                    } else {
                        text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                        text_matching_time_today.visibility = View.GONE
                    }
                }
        } else {
            //?????? ?????? ????????? ?????? ???
            btn_participate.visibility = View.VISIBLE

            db.collection("Matching").document(matchingDocumentId).collection("participant")
                .document(uid).get().addOnSuccessListener {
                    //????????? ???????????? ????????? ???
                    if (it.data == null) {
                        btn_participate.visibility = View.VISIBLE
                        btn_matching_cancle.visibility = View.GONE
                        btn_group_chat.visibility = View.GONE

                        db.collection("users").document(matchingLeaderUid)
                            .collection("userprofiles")
                            .document(matchingLeaderUid).get().addOnSuccessListener {
                                val result = it.toObject<UserProfile>()
                                Glide.with(this).load(result?.profilePhoto).circleCrop()
                                    .into(matching_profile_img)
                                matching_profile_name.setText(result?.userName)
                            }
                        db.collection("Matching").document(matchingDocumentId).get()
                            .addOnSuccessListener {
                                val result = it.toObject<Matching>()
                                matching_title.setText(result!!.title)
                                text_matching_place.setText("?????? : " + result!!.place + " " + result!!.place_detail)
                                val ongoing = result!!.ongoing
                                Log.d("ybyb", "ongoing -> ${ongoing}")

                                if (ongoing.equals(false)) {
                                    btn_Done.visibility = View.GONE
                                }
                                val date = result!!.date
                                val splitedDate = date.split("/")

                                //?????? ??????
                                val splitedStartTime = result!!.startime.split("/")

                                val start_time_minute =
                                    splitedStartTime[0].toInt() * 60 + splitedStartTime[1].toInt()

                                //?????? ??????
                                val splitedDoneTime = result!!.doneTime.split("/")
                                val Done_time_minute =
                                    splitedDoneTime[0].toInt() * 60 + splitedDoneTime[1].toInt()

                                if (curTime.equals(date)) {
                                    if (ongoing.equals(true)) {
                                        if (now_time_minute < start_time_minute) {
                                            val countDownTimer =
                                                object : CountDownTimer(200000, 1000) {
                                                    override fun onTick(millisUntilFinished: Long) {
                                                        text_matching_time_today.visibility =
                                                            View.VISIBLE
                                                        btn_Done.visibility = View.GONE
                                                        text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                                        text_matching_time_today.setText(
                                                            "??????" +
                                                                    getTime(
                                                                        splitedDate[1].toInt(),
                                                                        splitedDate[2].toInt(),
                                                                        splitedStartTime[0].toInt(),
                                                                        splitedStartTime[1].toInt()
                                                                    )
                                                        )
                                                    }

                                                    override fun onFinish() {}
                                                }
                                            countDownTimer.start()
                                        } else if (now_time_minute >= start_time_minute && now_time_minute <= Done_time_minute) {
                                            text_matching_time_today.visibility = View.VISIBLE
                                            text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                            text_matching_time_today.setText("?????? ???????????? ???????????????")
                                            Writer_profile.setBackgroundResource(R.drawable.round_mint)
                                            text_matching_time_today.setTextColor(R.color.main_mint.toInt())
                                        } else {
                                            text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                            text_matching_time_today.setText("?????? ????????? ???????????????.")
                                        }
                                    } else {
                                        text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                        text_matching_time_today.visibility = View.VISIBLE
                                        text_matching_time_today.setText("?????? ????????? ???????????????.")
                                    }
                                } else {
                                    text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                    text_matching_time_today.visibility = View.GONE
                                }
                            }


                        //????????? ???????????? ???
                    } else {
                        btn_participate.visibility = View.GONE
                        btn_matching_cancle.visibility = View.VISIBLE
                        btn_group_chat.visibility = View.VISIBLE

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
                                    "ybyb",
                                    "MatchingDetailActivity ..userprofile ???????????? ?????? : ${it}"
                                )
                            }
                        db.collection("Matching").document(matchingDocumentId).get()
                            .addOnSuccessListener {
                                val result = it.toObject<Matching>()
                                matching_title.setText(result!!.title)
                                text_matching_place.setText("?????? :" + result!!.place + "  " + result!!.place_detail)
                                val ongoing = result!!.ongoing
                                Log.d("ybyb", "ongoing -> ${ongoing}")

                                if (ongoing.equals(false)) {
                                    btn_Done.visibility = View.GONE
                                }
                                val date = result!!.date
                                val splitedDate = date.split("/")

                                //?????? ??????
                                val splitedStartTime = result!!.startime.split("/")
                                val start_time_minute =
                                    splitedStartTime[0].toInt() * 60 + splitedStartTime[1].toInt()

                                //?????? ??????
                                val splitedDoneTime = result!!.doneTime.split("/")
                                val Done_time_minute =
                                    splitedDoneTime[0].toInt() * 60 + splitedDoneTime[1].toInt()

                                if (curTime.equals(date)) {
                                    if (ongoing.equals(true)) {
                                        if (now_time_minute < start_time_minute) {
                                            val countDownTimer =
                                                object : CountDownTimer(200000, 1000) {
                                                    override fun onTick(millisUntilFinished: Long) {
                                                        text_matching_time_today.visibility =
                                                            View.VISIBLE
                                                        text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                                        text_matching_time_today.setText(
                                                            "??????" +
                                                                    getTime(
                                                                        splitedDate[1].toInt(),
                                                                        splitedDate[2].toInt(),
                                                                        splitedStartTime[0].toInt(),
                                                                        splitedStartTime[1].toInt()
                                                                    )
                                                        )
                                                    }

                                                    override fun onFinish() {}
                                                }
                                            countDownTimer.start()
                                        } else if (now_time_minute >= start_time_minute && now_time_minute <= Done_time_minute) {
                                            text_matching_time_today.visibility = View.VISIBLE
                                            text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                            text_matching_time_today.setText("?????? ???????????? ???????????????")
                                            Writer_profile.setBackgroundResource(R.drawable.round_mint)
                                            text_matching_time_today.setTextColor(R.color.main_mint.toInt())
                                        } else {
                                            text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                            text_matching_time_today.setText("?????? ????????? ???????????????.")
                                        }
                                    } else {
                                        text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                        text_matching_time_today.visibility = View.VISIBLE
                                        text_matching_time_today.setText("?????? ????????? ???????????????.")
                                    }
                                } else {
                                    text_matching_time.setText(splitedDate[1] + "???" + splitedDate[2] + "??? " + splitedStartTime[0] + "???" + splitedStartTime[1] + "???   (" + result!!.matchingTime + ")??? ??????")
                                    text_matching_time_today.visibility = View.GONE
                                }
                            }
                    }
                }
        }


        btn_matching_cancle.setOnClickListener {

            val dialog = CustomDialog_Cancle_Check(this)
            dialog.mydialog()
            dialog.setOnclickedListener(object :
                CustomDialog_Cancle_Check.ButtonClickListener {
                override fun onclickCancle() {
                    try {
                        //TODO ???????????? ?????? ????????? ????????? ??????
                        val intent = intent
                        intent.putExtra("title", matchingTitle)
                        intent.putExtra("documentId", matchingDocumentId)
                        intent.putExtra("uid", uid)
                        intent.putExtra("leaderuid", matchingLeaderUid)
                        intent.putExtra("preActivity", "MatchingDetailActivity")
                        finish() //?????? ???????????? ?????? ??????
                        overridePendingTransition(0, 0) //????????? ??????????????? ?????????
                        startActivity(intent) //?????? ???????????? ????????? ??????
                        overridePendingTransition(0, 0) //????????? ??????????????? ?????????


                        db.collection("Matching").document(matchingDocumentId)
                            .collection("participant")
                            .document(uid).delete()
                        db.collection("users").document(uid).collection("matching")
                            .document(matchingDocumentId).delete()

                        //????????????
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onlcickClose() {

                }
            })
        }
    }


    //?????? ?????? ???????????????
    private fun deleteDialog() {
        val dialog = CustomDialog_bbs_delete_check(this)
        val title = "?????? ??????"
        val description = "????????? ?????????????????????????"
        val action_text = "??????"
        dialog.mydialog(title, description, action_text)
        dialog.setOnclickedListener(object :
            CustomDialog_bbs_delete_check.ButtonClickListener {
            override fun onclickAction() {
                try {
                    matchingDelete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onlcickClose() {

            }
        })
    }

    //?????? ?????? ???????????????
    private fun DoneDialog() {
        val dialog = CustomDialog_bbs_delete_check(this)
        val title = "?????? ??????"
        val description = "????????? ?????????????????????????"
        val action_text = "??????"
        dialog.mydialog(title, description, action_text)
        dialog.setOnclickedListener(object :
            CustomDialog_bbs_delete_check.ButtonClickListener {
            override fun onclickAction() {
                try {
                    Matching_Done()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onlcickClose() {

            }
        })
    }

    private fun matchingDelete() {
        val matchingDocumentId = intent.getStringExtra("documentId").toString()

        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val uids = document.get("uid").toString()
                db.collection("users").document(uids).collection("matching")
                    .document(matchingDocumentId).delete().addOnSuccessListener {
                        Log.d("ybyb", "users??? ?????? ??????")
                    }
            }
        }

        db.collection("Matching").document(matchingDocumentId).delete().addOnSuccessListener {
            Log.d("yb", "ybyb ?????? ?????? ??????")
            val intent = Intent(this@MatchingDetailActivity, MainMenuActivity::class.java)
            intent.putExtra("state", "matching")
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
            finish()

        }
    }

    //?????? ?????????
    private fun goChatting() {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        if (intent.hasExtra("leaderuid")) {
            //????????? ?????? ?????? uid
            val leaderUid = intent.getStringExtra("leaderuid")
            //????????? title
            val matchingtitle = intent.getStringExtra("title").toString()
        }
    }

    //?????? ????????? ?????????
    private fun getTime(month: Int, day: Int, hour: Int, minute: Int): String? {
        val date = Date()
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val c_hour = calendar[Calendar.HOUR_OF_DAY]
        val c_min = calendar[Calendar.MINUTE]
        val c_sec = calendar[Calendar.SECOND]


        val baseCal: Calendar = GregorianCalendar(year, month, day, c_hour, c_min, c_sec)
        val targetCal: Calendar = GregorianCalendar(year, month, day, hour, minute, 0) //??????????????????
        val diffSec = (targetCal.timeInMillis - baseCal.timeInMillis) / 1000
        val diffDays = diffSec / (24 * 60 * 60)
        targetCal.add(Calendar.DAY_OF_MONTH, (-diffDays).toInt())
        val hourTime = Math.floor((diffSec / 3600).toDouble()).toInt()
        val minTime = Math.floor(((diffSec - 3600 * hourTime) / 60).toDouble()).toInt()
        val secTime =
            Math.floor((diffSec - 3600 * hourTime - 60 * minTime).toDouble()).toInt()
        val hour = String.format("%02d", hourTime)
        val min = String.format("%02d", minTime)
        val sec = String.format("%02d", secTime)
        return "???????????? " + hour + " ?????? " + min + " ??? " + sec + "??? ???????????????."
    }

    //????????? ??? : ?????? ???????????? ????????? ????????? XML????????????


    //?????? ?????????
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
                    Log.d("ybyb", "MatchingDetailActivity_participant  ??????")

                    db.collection("users").document(matchingLeaderUid)
                        .collection("userprofiles").document(matchingLeaderUid)
                        .get()
                        .addOnSuccessListener {
                            val result = it.toObject<UserProfile>()
                            val receiverToken = result?.userToken
                            send_fcm(matchingTitle, "?????? ???????????? ??????????????????.", receiverToken.toString())
                        }
                }.addOnFailureListener {
                    Log.d("ybyb", "Participant ?????? ?????? : ${it}")
                }

            db.collection("users").document(uid).collection("matching").document(documentId)
                .set(participant_user).addOnSuccessListener {
                    Log.d("ybyb", "MatchingDetailActivity_participant_user  ??????")

                }.addOnFailureListener {
                    Log.d("ybyb", "Participant_inUser?????? ?????? : ${it}")
                }

            db.collection("users").document(uid).collection("dogprofiles")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.e("yy", "???????  " + document.toString())
                        val dogs = document.toObject<DogProfile>()
                        for (i in dogname) {
                            Log.e("yy", "i=" + i)
                            Log.e("yy", "dogName=" + dogs.dogName)
                            if (dogs.dogName == i) {
                                Log.e("yy", dogs.dogName)

                                db.collection("Matching").document(documentId)
                                    .collection("participant").document(uid)
                                    .collection("dogprofile").document(i)
                                    .set(
                                        Dog_Profile_Item(
                                            dogs.uid,
                                            document.id,//?
                                            dogs?.dogAge,
                                            dogs?.dogName,
                                            dogs?.dogBreed,
                                            dogs?.dogSex,
                                            dogs?.neutering,
                                            dogs?.photoUrl.toString()
                                        )
                                    )
                                    .addOnSuccessListener {
                                        Log.d(
                                            "Participant",
                                            "MatchingDetailActivity_participant dog  ??????"
                                        )
                                    }
                            }
                        }
                    }
                }

            val dialog = CustomDialog(this)
            dialog.mydialog()
            dialog.setOnclickedListener(object : CustomDialog.ButtonClickListener {
                override fun onclickMyMatchingList() {
                    val intent =
                        Intent(this@MatchingDetailActivity, MyMatchingListActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
                    finish()

                }

                override fun onlcickClose() {
                    try {
                        //TODO ???????????? ?????? ????????? ????????? ??????
                        val intent = intent
                        intent.putExtra("title", matchingtitle)
                        intent.putExtra("documentId", documentId)
                        intent.putExtra("uid", uid)
                        intent.putExtra("leaderuid", matchingLeaderUid)
                        intent.putExtra("preActivity", "MatchingDetailActivity")
                        finish() //?????? ???????????? ?????? ??????
                        overridePendingTransition(0, 0) //????????? ??????????????? ?????????
                        startActivity(intent) //?????? ???????????? ????????? ??????
                        overridePendingTransition(0, 0) //????????? ??????????????? ?????????
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            })

        } else {

        }

    }

    //????????? ???????????? ???????????? ??? ????????????
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 110) {  // ????????? ?????? ??? ????????? ????????? uid??? ?????????
                dogname = data?.getStringArrayListExtra("select_dogname") as ArrayList<String>
                Log.d("yy", "????????? ????????? ?????????Q!" + dogname.toString())
                //????????? ????????? ??????????????? ??????
                db.collection("Matching").document(matchingDocumentId).get()
                    .addOnSuccessListener {
                        matching_dog_neu = it.get("condition_dog_neutralization").toString()
                        if (matching_dog_neu == "all,neutralization") {
                            matching_dog_neu = "true"
                        } else {
                            matching_dog_neu = "true,false"
                        }
                        matching_dog_size = it.get("condition_dog_size").toString()
                        if (matching_dog_size == "all") {
                            matching_dog_size = "big,small,middle"
                        }
                        matching_owner_gender = it.get("condition_owner_gender").toString()
                        if (matching_owner_gender == "all") {
                            matching_owner_gender = "wm,man"
                        }
                        condition()
                    }
            }
        }
    }

    fun condition() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid.toString()
        var check = ArrayList<String>()
        db.collection("users").document(uid).collection("userprofiles")
            .document(uid).get()
            .addOnSuccessListener {
                if (matching_owner_gender.contains(it.get("userSex").toString())) {
                    db.collection("users").document(uid).collection("dogprofiles")
                        .get()
                        .addOnSuccessListener {
                            for (document in it) {
                                for (name in dogname) {
                                    if (document.get("dogName").toString() == name) {
                                        if (matching_dog_size.contains(
                                                document.get("dogSize").toString()
                                            )
                                            && matching_dog_neu.contains(
                                                document.get("neutering").toString()
                                            )
                                        ) {
                                            check.add("true")
                                        } else {
                                            check.add("false")
                                        }
                                    }
                                }
                            }
                            if (check.contains("false")) {
                                Toast.makeText(
                                    this,
                                    "????????? ????????? ???????????? ????????? ???????????? ????????????",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                participation()
                            }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "?????? ????????? ???????????? ????????? ???????????? ????????????",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

//    private fun changedDocument(bbs_oid: String) {
//        auth = FirebaseAuth.getInstance()
//        val uid = auth.uid.toString()
//        db.collection(bbs_oid).get().addOnSuccessListener {
//            for (document in it) {
//                if (uid.equals(document.get("uid").toString())) {
//                    db.collection(bbs_oid).document(document.id).collection("Comment")
//                        .addSnapshotListener { value, error ->
//                            if (error != null) {
//                                Log.w(
//                                    "ybyb",
//                                    "${bbs_oid} ????????? snapshot ?????? : ${error.message}"
//                                )
//                                return@addSnapshotListener
//                            }
//                            if (value!!.metadata.isFromCache) return@addSnapshotListener
//                            for (doc in value.documentChanges) {
//                                //documet ??? ????????? ??????????????? ???
//                                if (doc.type == DocumentChange.Type.ADDED) {
//                                    if (!uid.equals(document.get("uid").toString())) {
//                                        sendNotification(
//                                            document.get("title").toString() + " ???????????? ????????? ???????????????.",
//                                            doc.document["comment"].toString()
//                                        )
//                                        Log.d("ybyb", "????????? ????????? ??????")
//                                    }
//                                    Log.d("ybyb", "??? ???????????? ??? ??????")
//                                }
//
//                            }
//                        }
//                }
//            }
//        }
//    }

    //    //?????? ????????? ??????..--> ?????? ??? ???????
//    //????????? ?????? ????????? ???????????? ??? ?????? ??????//
//    private fun matchingAdded() {
//        auth = FirebaseAuth.getInstance()
//        db = FirebaseFirestore.getInstance()
//        val uid = auth.currentUser?.uid.toString()
//
//        db.collection("Matching").get().addOnSuccessListener {
//            for (document in it) {
//                if (uid.equals(document.get("uid").toString())) {
//                    db.collection("Matching").document(document.id).collection("participant")
//                        .addSnapshotListener { value, error ->
//                            if (error != null) {
//                                Log.w(
//                                    "ybyb",
//                                    "?????? ?????? ????????? snapshot ?????? : ${error.message}"
//                                )
//                                return@addSnapshotListener
//                            }
//                            if (value!!.metadata.isFromCache) return@addSnapshotListener
//                            for (doc in value.documentChanges) {
//                                //documet ??? ????????? ??????????????? ???
//                                if (doc.type == DocumentChange.Type.ADDED) {
//                                    if (!uid.equals(doc.document["uid"].toString())) {
//                                        sendNotification(
//                                            document.get("title").toString(),
//                                            "?????? ????????? ???????????? ?????????????????????."
//                                        )
//                                        Log.d("ybyb", "(??????)?????? ??????")
//                                    }
//                                } else if (doc.type == DocumentChange.Type.REMOVED) {
//                                    sendNotification(
//                                        document.get("title").toString(),
//                                        "?????? ???????????? ???????????? ???????????????."
//                                    )
//                                    Log.d("ybyb", "(??????)?????? ??????")
//                                }
//                            }
//                        }
//                }
//            }
//        }
//    }
//
    private fun send_fcm(title: String, content: String, receiverToken: String) {
        Fire.create()
            .setTitle(title)
            .setBody(content)
            .setCallback { pushCallback, exception ->
                Log.d("ybyb", "push->${pushCallback}")
                Log.d("ybyb", "???????????????: ${pushCallback.isSent}")
                Log.d("ybyb", "e->${exception.toString()}")

            }
            .toIds(receiverToken)  //toTopic("FOR TOPIC") or toCondition("CONDITION HERE")
            .push()
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = mLM!!.getLastKnownLocation(mProvider)
        if (location != null) {
            mListener.onLocationChanged(location)
        }
        mLM!!.requestSingleUpdate(mProvider, mListener, null)

    }

    override fun onStop() {
        super.onStop()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mLM!!.removeUpdates(mListener)
    }

    override fun onResume() {
        super.onResume()

    }

    private fun moveMap(lat: Double, lng: Double) {
        if (map != null) {
            val latLng = LatLng(lat, lng)
            val position = CameraPosition.Builder()
                .target(latLng)
                .bearing(30f)
                .tilt(45f)
                .zoom(17f)
                .build()
            val update =
                CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            //            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            map!!.moveCamera(update)
            //        map.animateCamera(update);
        }
    }

    var mListener: LocationListener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
            }

            override fun onStatusChanged(
                s: String,
                i: Int,
                bundle: Bundle
            ) {
            }

            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {}
        }

    override fun onMapReady(googleMap: GoogleMap) {
        db = FirebaseFirestore.getInstance()
        db.collection("Matching").document(matchingDocumentId)
            .get()
            .addOnSuccessListener {
                val result = it.toObject<Matching>()
                mylocation = LatLng(
                    result!!.latitude,
                    result!!.longitude
                )
                map = googleMap
                map!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@addOnSuccessListener
                }
                map!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            mylocation!!.latitude,
                            mylocation!!.longitude
                        ), 17f
                    )
                )
                addMarker(
                    mylocation!!.latitude,
                    mylocation!!.longitude,
                    ""
                )

//        map!!.isMyLocationEnabled = true
                val mapui = map!!.uiSettings
                mapui.isZoomControlsEnabled = false
                mapui.isMapToolbarEnabled = false
                mapui.isZoomGesturesEnabled = false
                mapui.isTiltGesturesEnabled = false
                mapui.isRotateGesturesEnabled = false
                mapui.isScrollGesturesEnabled = false
                mapui.isScrollGesturesEnabledDuringRotateOrZoom =
                    false
                mapui.isCompassEnabled = false
            }

    }

//    private fun sendNotification(title: String, content: String) {
//        val sendTime_now = (SystemClock.elapsedRealtime() + 1000)
//        //calendar.timeInMillis
//
//        val alarmIntent = Intent(this, MyReceiver::class.java).apply {
//            action = "com.check.up.setAlarm"
//            putExtra("title", title)
//            putExtra("content", content)
//        }
//        val alarmManager =
//            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            0,
//            alarmIntent,
//            PendingIntent.FLAG_CANCEL_CURRENT
//        )
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.ELAPSED_REALTIME,
//                sendTime_now,
//                pendingIntent
//            )
//
//        } else {
//            if (Build.VERSION.SDK_INT >= 19) {
//                alarmManager.setExact(
//                    AlarmManager.ELAPSED_REALTIME,
//                    sendTime_now,
//                    pendingIntent
//                )
//            } else {
//                alarmManager.set(
//                    AlarmManager.ELAPSED_REALTIME,
//                    sendTime_now,
//                    pendingIntent
//                )
//            }
//        }
//    }

    private fun Matching_Done() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid

        db.collection("Matching").document(matchingDocumentId).update("ongoing", false)
            .addOnSuccessListener {
                Log.d("ybyb", "?????? ????????????")

                val intent = Intent(this, MainMenuActivity::class.java)
                intent.putExtra("state", intent.getStringExtra("matching").toString())
                finish() //?????? ???????????? ?????? ??????
                overridePendingTransition(0, 0) //????????? ??????????????? ?????????
                startActivity(intent) //??????
            }.addOnFailureListener {
                Log.d("ybyb", "?????? ?????? ??????")
            }

        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val uids = document.get("uid").toString()
                db.collection("users").document(uids).collection("matching")
                    .document(matchingDocumentId).delete().addOnSuccessListener {
                        Log.d("ybyb", "users??? ?????? ??????")
                    }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(): LatLng {
        val lm =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean =
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean =
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        when {
            isNetworkEnabled -> {
                val location =
                    lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val getLongtitude = location?.longitude
                val getLatitude = location?.latitude
                Log.e(
                    "joo",
                    "GPSEnabled - ?????? :${getLatitude.toString()}  ?????? :${getLongtitude.toString()}"
                )
                var currentLocation =
                    LatLng(getLongtitude!!, getLatitude!!)

                return currentLocation
            }
            isGPSEnabled -> {
                val location =
                    lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val getLongtitude = location?.longitude
                val getLatitude = location?.latitude
                Log.e(
                    "joo",
                    "GPSEnabled - ?????? :${getLatitude.toString()}  ?????? :${getLongtitude.toString()}"
                )

                var currentLocation =
                    LatLng(getLongtitude!!, getLatitude!!)

                return currentLocation
            }

            else -> {
                Toast.makeText(
                    this,
                    "GPS ?????? ??????",
                    Toast.LENGTH_SHORT
                ).show()
                return LatLng(37.537523, 126.96558)
            }
        }
    }

    var marker: Marker? = null
    private fun addMarker(
        lat: Double,
        lng: Double,
        title: String
    ) {
        if (marker != null) {
            marker!!.remove()
            marker = null
        }
        val options = MarkerOptions()
        options.position(LatLng(lat, lng))
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mappoint))
        options.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ORANGE
            )
        )
        options.title(title)
        options.draggable(true)
        options.snippet("snippet - $title")
        marker = map!!.addMarker(options)
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
            0 -> return MatchingExplanationFragment()
            1 -> return ParticipantListFragment()
            else -> return MatchingExplanationFragment()
        }
    }
}
