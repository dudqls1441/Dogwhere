package com.capstone.dogwhere.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.capstone.dogwhere.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var  auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        auth = FirebaseAuth.getInstance()
        val myUid = auth.uid
        val yourUid = intent.getStringExtra("yourUid")
        val name = intent.getStringExtra("name")

        val adapter = GroupAdapter<GroupieViewHolder>()


        val db = FirebaseFirestore.getInstance()

        db.collection("message")
            .orderBy("time")
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    Log.d("joo", document.toString())

                    val senderUid = document.get("myUid")
                    val msg = document.get("message")

                    Log.e("joo", senderUid.toString())
                    Log.e("joo", myUid.toString())
                    Log.e("joo", yourUid.toString())

                    if(senderUid!!.equals(myUid)){
                        Log.e("joo", "hello")
                        adapter.add(ChatRightMe())
                    }else{
                        adapter.add(ChatLeftYou())
                    }

                }
            recyclerview_chatRoom.adapter = adapter
            }

        send_btn.setOnClickListener {
            val message = message_edit.text.toString()

            message_edit.setText("")

            val chat = ChatModel(myUid.toString(), yourUid.toString(), message, System.currentTimeMillis())

            db.collection("message")
                .add(chat)
                .addOnSuccessListener {
                    Log.e("joo", "성공")
                }
                .addOnFailureListener {
                    Log.e("joo", "실패")
                }
        }

    }
}