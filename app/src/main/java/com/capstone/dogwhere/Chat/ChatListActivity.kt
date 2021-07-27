package com.capstone.dogwhere.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.capstone.dogwhere.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_list.*

class ChatListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        myChatList.setOnClickListener {
            val intent = Intent(this, MyRoomActivity::class.java)
            startActivity(intent)
        }

        val adapter = GroupAdapter<GroupieViewHolder>()

        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for(document in result){
                    adapter.add(UserItem(document.get("userName").toString(), document.get("uid").toString()))
                    Log.e("joo", document.get("userName").toString())
                    Log.e("joo", "${document.id} => ${document.data}")
                }
                recyclerview_list.adapter = adapter

            }
            .addOnFailureListener { exception ->
                Log.e("joo", "Error getting documents", exception)
            }

        adapter.setOnItemClickListener{ item, view ->

            val uid = (item as UserItem).uid
            val name = (item as UserItem).name

            val intent = Intent(this, ChatRoomActivity::class.java)
            Log.e("joo", "yourUid:"+uid+"  name:"+name)
            intent.putExtra("yourUid", uid)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }
}