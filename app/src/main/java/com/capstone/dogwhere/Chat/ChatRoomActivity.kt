package com.capstone.dogwhere.Chat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.capstone.dogwhere.MainMenuActivity
import com.capstone.dogwhere.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_chatting_room.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.chatting_receive_item.*
import kotlinx.android.synthetic.main.chatting_send_item.*
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var  auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_room)

        auth = FirebaseAuth.getInstance()
        val myUid = auth.uid
        val yourUid = intent.getStringExtra("yourUid")
        val name = intent.getStringExtra("name")

        val adapter = GroupAdapter<GroupieViewHolder>()

        val db = FirebaseFirestore.getInstance()

//        db.collection("message")
//            .orderBy("time")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result){
//                    Log.d("joo", document.toString())
//
//                    val senderUid = document.get("myUid")
//                    val msg = document.get("message").toString()
//
//                    Log.e("joo", senderUid.toString())
//                    Log.e("joo", myUid.toString())
//                    Log.e("joo", yourUid.toString())
//
//                    if(senderUid!!.equals(myUid)){
//                        Log.e("joo", "hello")
//                        adapter.add(ChatRightMe(msg))
//                    }else{
//                        adapter.add(ChatLeftYou(msg))
//                    }
//
//                }
//            recyclerview_chatRoom.adapter = adapter
//            }


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

                // 현재 시간을 Date 타입으로 변환
                val t_date = time?.let { Date(it.toLong()) }

                // 날짜, 시간을 가져오고 싶은 형태 선언
                val t_dateFormat = SimpleDateFormat("h:mm a", Locale("ko", "KR"))

                // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
                val str_date = t_dateFormat.format(t_date)


                if (who == "me") {
                    adapter.add(ChatRightMe(msg.toString(), str_date.toString()))
                } else {
                    adapter.add(ChatLeftYou(name.toString(), msg.toString(), str_date.toString()))
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
        recycler_chatroom_view.scrollToPosition(recycler_chatroom_view.itemDecorationCount -1)
        recycler_chatroom_view.adapter = adapter
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
                name.toString(),
                message,
                System.currentTimeMillis(),
                "you"
            )
            myRef.child(yourUid.toString()).child(myUid.toString()).push().setValue(chat_get)

            edittext_chatroom_msg.setText("")
//
//
//            db.collection("message")
//                .add(chat)
//                .addOnSuccessListener {
//                    Log.e("joo", "성공")
//                }
//                .addOnFailureListener {
//                    Log.e("joo", "실패")
//                }
        }

    }
    fun hideKeyboard() {
        val hide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(btn_chatroom_send.windowToken, 0)
    }

}