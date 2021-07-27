package com.capstone.dogwhere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_b_b_s__common_post.*
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__post.*
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__post.post_content
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__post.post_title
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__post.writer_img
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__post.writer_name
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__post.writer_time
import kotlinx.android.synthetic.main.navi_header.*

class BBS_Transaction_Post : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s__transaction__post)

        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore


        post_title.setText(intent.getStringExtra("title"))
        post_content.setText(intent.getStringExtra("content"))
        writer_name.setText(intent.getStringExtra("name"))
        writer_time.setText(intent.getStringExtra("time"))
        post_price.setText(intent.getStringExtra("price"))


        db.collection("users").document(intent.getStringExtra("uid").toString()).collection("userprofiles").document(intent.getStringExtra("uid").toString()).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<UserProfile>()
                Log.e("joo","uidddddd"+intent.getStringExtra("uid").toString())
                Glide.with(this).load(result?.profilePhoto).into(writer_img)
            }

    }
}