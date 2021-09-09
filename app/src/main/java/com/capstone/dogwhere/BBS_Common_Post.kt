package com.capstone.dogwhere

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.BBS_Comment
import com.capstone.dogwhere.DTO.BBS_CommentItem
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_b_b_s__common_post.*
import java.text.SimpleDateFormat
import java.util.*


class BBS_Common_Post : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var commentnickname : String
    private lateinit var commentProfile : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s__common_post)

        auth = FirebaseAuth.getInstance()
        val uid = auth.uid.toString()
        val adapter = GroupAdapter<GroupieViewHolder>()
        val bbs_tabname = intent.getStringExtra("tab").toString()
        val bbs_title = intent.getStringExtra("title").toString()
        val bbs_oid = intent.getStringExtra("oid").toString()

        // 게시글 작성자 프로필 사진 가져오기
        db.collection("users").document(intent.getStringExtra("uid").toString()).collection("userprofiles").document(intent.getStringExtra("uid").toString()).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<UserProfile>()
                Log.e("joo","uidddddd"+intent.getStringExtra("uid").toString())
                Glide.with(this).load(result?.profilePhoto).into(writer_img)
            }

        // 댓글 출력
        db.collection(bbs_tabname).document(bbs_oid).collection("Comment").orderBy("time").get()
            .addOnSuccessListener { result ->
            for (document in result) {
                val commentitem = BBS_CommentItem(
                    document.get("uid").toString(),
                    document.get("comment").toString(),
                    document.get("username").toString(),
                    document.get("time").toString(),
                    document.get("userprofile").toString()
                    )
                adapter.add(commentitem)
                Log.e("joo", commentitem.profile)
            }
            recycler_bbscommon_comment?.adapter = adapter
        }




        // 댓글 작성 이벤트
        btn_bbscommon_send.setOnClickListener {
            if(edittext_bbscommon_comment.text.toString() == ""){
            } else{
                db.collection("users").document(uid).collection("userprofiles").document(uid).get()
                    .addOnSuccessListener { result ->
                        val result = result.toObject<UserProfile>()
                        Log.e("joo",result.toString())
                        commentnickname = result?.userName.toString()
                        commentProfile = result?.profilePhoto.toString()
                        Log.e("joo","nickname,, profile : "+commentProfile+commentnickname )

                        postComment(bbs_tabname, bbs_oid, commentnickname, commentProfile)

                        try {
                            //TODO 액티비티 화면 재갱신 시키는 코드
                            val intent = intent
                            finish() //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                            startActivity(intent) //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            }
        }


        post_title.setText(intent.getStringExtra("title"))
        post_content.setText(intent.getStringExtra("content"))
        writer_name.setText(intent.getStringExtra("name"))
        writer_time.setText(intent.getStringExtra("time"))


    }

    // 게시물 댓글 입력 메서드
    private fun postComment(bbs_tab : String, bbs_oid : String, nickname : String, profile : String){

        val uid = auth.uid.toString()
        val comment = edittext_bbscommon_comment.text.toString()
        val time = currenttime().toString()
        val bbscomment = BBS_Comment(uid, comment, nickname, time, profile)

        val doc = db.collection(bbs_tab).document(bbs_oid).collection("Comment").document()
        Log.e("joo", "postComment id :"+ doc.id)
        doc.set(bbscomment)
        edittext_bbscommon_comment.setText("")
    }

    private fun currenttime(): String? {

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
        val curTime = dateFormat.format(Date(time))

        return curTime
    }
}