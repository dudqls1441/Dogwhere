package com.capstone.dogwhere.GroupChat

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dogwhere.DTO.UserProfile
import com.capstone.dogwhere.FCM.MyReceiver
import com.capstone.dogwhere.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chatting_room.*
import java.text.SimpleDateFormat
import java.util.*

class GroupChatRoomActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chatting_room)

        auth = FirebaseAuth.getInstance()
        val myUid = auth.uid
        val yourUid = intent.getStringExtra("yourUid")
        val name = intent.getStringExtra("name")
        val documentId = intent.getStringExtra("matchingDocumentId").toString()
        val matchingTitle = intent.getStringExtra("matchingTitle")
        val adapter = GroupAdapter<GroupieViewHolder>()
        var myname: String? = null
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(myUid.toString()).collection("userprofiles")
            .document(myUid.toString())
            .get().addOnSuccessListener {
                val result = it.toObject<UserProfile>()
                myname = result?.userName.toString()
            }
        chatting_room_other.text = matchingTitle
        val database = FirebaseDatabase.getInstance()
        val groupChatRef = database.getReference("group-chat").child(documentId)

        groupChatRef.child("participant").get().addOnSuccessListener {
            Log.d("ybyb", "participant ->${it.children}")
            it.children.forEach {
                Log.d("ybyb", "child ->${it.toString()}")
            }
        }


        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("joo", "snapshot: " + snapshot)

                val model = snapshot.getValue(GroupChatNewModel::class.java)
                val msg = model?.message
                val writerUid = model?.writerUid
                val time = model?.time
                Log.d(
                    "ybyb",
                    "msg ->${msg} ,writerUid ->${writerUid}, time ->${time} documentId ->${documentId} "
                )

                // 현재 시간을 Date 타입으로 변환
                val t_date = time?.let { Date(it.toLong()) }

                // 날짜, 시간을 가져오고 싶은 형태 선언
                val t_dateFormat = SimpleDateFormat("h:mm a", Locale("ko", "KR"))

                // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
                val str_date = t_dateFormat.format(t_date)
                db.collection("users").document(writerUid.toString())
                    .collection("userprofiles").document(writerUid.toString())
                    .get()
                    .addOnSuccessListener {
                        val result = it.toObject<UserProfile>()
                        val nickname = result?.userName
                        val profilephoto = result?.profilePhoto
                        if (writerUid == myUid) {
                            adapter.add(ChatRightMe(msg.toString(), str_date.toString()))
                        } else {
                            adapter.add(
                                ChatLeftYou(
                                    nickname.toString(),
                                    msg.toString(),
                                    str_date.toString(),
                                    profilephoto.toString()
                                )
                            )
                            sendNotification(
                                nickname.toString() + "님에게 메시지가 도착했습니다.",
                                msg.toString(),
                                yourUid.toString()
                            )
                        }
                        recycler_chatroom_view.scrollToPosition(adapter.itemCount - 1)
                        recycler_chatroom_view.adapter = adapter
                    }


                Log.d("ybyb", "p0: " + msg)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        groupChatRef.addChildEventListener(childEventListener)

        val myRef_list = database.getReference("group-chat").child("ExampleGroupId")

        btn_chatroom_send.setOnClickListener {


            val message = edittext_chatroom_msg.text.toString()

            val chat = GroupChatNewModel(
                myUid.toString(),
                documentId,
                name.toString(),
                message,
                System.currentTimeMillis()
            )
            groupChatRef.push().setValue(chat)

            edittext_chatroom_msg.setText("")
        }

    }

    fun hideKeyboard() {
        val hide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(btn_chatroom_send.windowToken, 0)
    }

    private fun sendNotification(title: String, content: String, senderUid: String) {
        val sendTime_now = (SystemClock.elapsedRealtime() + 500)
        //calendar.timeInMillis
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid

        val alarmIntent = Intent(this, MyReceiver::class.java).apply {
            action = "com.check.up.setAlarm"
            putExtra("title", title)
            putExtra("content", content)
            putExtra("senderUid", senderUid)
        }
        val alarmManager =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                sendTime_now,
                pendingIntent
            )

        } else {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    sendTime_now,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    sendTime_now,
                    pendingIntent
                )
            }
        }
    }

}