package com.capstone.dogwhere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.capstone.dogwhere.DTO.MyNotificationList_item
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_notification_list.*

class NotificationListActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)


        init()
    }

    private fun init() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val adapter = GroupAdapter<GroupieViewHolder>()
        val uid = auth.currentUser!!.uid


        db.collection("users").document(uid).collection("notification").get().addOnSuccessListener {
            for(document in it){
                adapter.add(
                    MyNotificationList_item(
                        document.get("uid").toString(),
                        document.get("topic").toString(),
                        document.get("username").toString(),
                        document.get("message_content").toString(),
                        document.get("list_time").toString()
                    )
                )
                Log.d("ybybyb","notifi data -> ${document.data}")
                recycler_notification.adapter=adapter
            }
        }




    }
}