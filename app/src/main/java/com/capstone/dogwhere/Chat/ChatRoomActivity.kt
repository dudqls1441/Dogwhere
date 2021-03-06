package com.capstone.dogwhere.Chat

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
import com.firepush.Fire
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

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_room)

        Fire.init("AAAA1P59Tgs:APA91bEuZ_Hp7rsbkRmR0zWrI_uDhd9o3RMXz4oBpOeXHGc_RCJEo_-d1J-_BL5Hl4jk0KmzjZmWzzNeCOJ4n8jsiFo53QNaknXCq4fOwvbkuSpXNF08XMYud8dY8fHPl1PDMj8-_EDU")


        auth = FirebaseAuth.getInstance()
        val myUid = auth.uid
        val yourUid = intent.getStringExtra("yourUid")
        val name = intent.getStringExtra("name")
        val adapter = GroupAdapter<GroupieViewHolder>()
        var myname: String? = null
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(myUid.toString()).collection("userprofiles")
            .document(myUid.toString())
            .get().addOnSuccessListener {
                val result = it.toObject<UserProfile>()
                myname = result?.userName.toString()
            }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        val readRef =
            database.getReference("message").child(myUid.toString()).child(yourUid.toString())


        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("joo", "snapshot: " + snapshot)

                val model = snapshot.getValue(ChatNewModel::class.java)
                val msg = model?.message
                val who = model?.who
                val nickname = model?.nickname
                val time = model?.time

                // ?????? ????????? Date ???????????? ??????
                val t_date = time?.let { Date(it.toLong()) }

                // ??????, ????????? ???????????? ?????? ?????? ??????
                val t_dateFormat = SimpleDateFormat("h:mm a", Locale("ko", "KR"))

                // ?????? ????????? dateFormat ??? ????????? ????????? String ?????? ??????
                val str_date = t_dateFormat.format(t_date)
                db.collection("users").document(yourUid.toString())
                    .collection("userprofiles").document(yourUid.toString())
                    .get()
                    .addOnSuccessListener {
                        val result = it.toObject<UserProfile>()
                        val nickname = result?.userName
                        val receiverToken = result?.userToken
                        if (who == "me") {
                            adapter.add(ChatRightMe(msg.toString(), str_date.toString()))
                        } else {
                            adapter.add(
                                ChatLeftYou(
                                    nickname.toString(),
                                    msg.toString(),
                                    str_date.toString(),
                                    receiverToken.toString()
                                )
                            )
                        }
                        recycler_chatroom_view.scrollToPosition(adapter.itemCount - 1)
                        recycler_chatroom_view.adapter = adapter
                    }


                Log.d("joo", "p0: " + msg)
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

        readRef.addChildEventListener(childEventListener)

        val myRef_list = database.getReference("message-user-list")

        btn_chatroom_send.setOnClickListener {


            val message = edittext_chatroom_msg.text.toString()

            val chat = ChatNewModel(
                myUid.toString(),
                yourUid.toString(),
                name.toString(),
                message,
                System.currentTimeMillis(),
                "me"
            )
            myRef.child(myUid.toString()).child(yourUid.toString()).push().setValue(chat)

            myRef_list.child(myUid.toString()).child(yourUid.toString()).setValue(chat)

            val chat_get = ChatNewModel(
                yourUid.toString(),
                myUid.toString(),
                myname.toString(),
                message,
                System.currentTimeMillis(),
                "you"
            )
            myRef.child(yourUid.toString()).child(myUid.toString()).push().setValue(chat_get)
            myRef_list.child(yourUid.toString()).child(myUid.toString()).setValue(chat_get)
                .addOnSuccessListener {
                    Log.d("ybyb", "?????? ?????? ??????")
                    db.collection("users").document(yourUid.toString())
                        .collection("userprofiles").document(yourUid.toString())
                        .get()
                        .addOnSuccessListener {
                            val result = it.toObject<UserProfile>()
                            val receiverToken = result?.userToken
                            Log.d("ybyb", "????????? ?????? ->${receiverToken.toString()}")
                            send_fcm(
                                myname.toString() + "????????? ???????????? ??????????????????.",
                                message.toString(),
                                receiverToken.toString()
                            )
                        }
                }
            edittext_chatroom_msg.setText("")

        }

    }

    fun hideKeyboard() {
        val hide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(btn_chatroom_send.windowToken, 0)
    }

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
}