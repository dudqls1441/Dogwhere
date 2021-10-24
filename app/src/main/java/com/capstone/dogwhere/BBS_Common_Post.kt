package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.dogwhere.BBS.HeartPost
import com.capstone.dogwhere.Chat.ChatRoomActivity
import com.capstone.dogwhere.DTO.BBS_Comment
import com.capstone.dogwhere.DTO.BBS_CommentItem
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
    val adapter = GroupAdapter<GroupieViewHolder>()
    var heartFlag : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s__common_post)

        val bbs_oid = intent.getStringExtra("oid").toString()
        val bbs_tabname = intent.getStringExtra("tab").toString()
        Log.e("joo", "bbs_oid, bbs_tabname - " + bbs_oid + bbs_tabname )
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid.toString()
        val name = intent.getStringExtra("name").toString()
        val your_uid = intent.getStringExtra("uid").toString()


        if(uid==your_uid){
            btn_trash.visibility= View.VISIBLE
            btn_trash.setOnClickListener {
                deleteDialog(bbs_tabname,bbs_oid)
            }
        }
        // 게시글 작성자 프로필
        getWriterProfile()
        // Like 유무 확인
        showHeart(uid, bbs_oid)
        // 댓글 출력 이벤트
        getComment(bbs_tabname, bbs_oid)
        // 댓글 작성 이벤트
        btn_bbscommon_send.setOnClickListener {
            if(edittext_bbscommon_comment.text.toString() != ""){
                writeComment(uid, bbs_tabname, bbs_oid)
            }
        }
        // 게시물 하트 버튼 이벤트
        img_bbsCommon_writerLike.setOnClickListener{
            heartClick(uid, bbs_oid, bbs_tabname)
        }

        img_bbsCommon_writerChat.setOnClickListener {
            val bbs_uid = intent.getStringExtra("uid").toString()
            val bbs_name = intent.getStringExtra("name").toString()
            val intent = Intent(this, ChatRoomActivity::class.java)
            Log.e("joo", "yourUid:"+bbs_uid+"  name:"+bbs_name)
            intent.putExtra("yourUid", bbs_uid)
            intent.putExtra("name", bbs_name)
            startActivity(intent)
        }

        img_bbsCommon_writer.setOnClickListener {
            Intent(this, UserProfileActivity::class.java).apply {
                putExtra("name", name)
                putExtra("uid", your_uid)
            }.run { startActivity(this) }
        }

        btn_back.setOnClickListener {
            finish()
        }

        post_title.setText(intent.getStringExtra("title"))
        post_content.setText(intent.getStringExtra("content"))
        writer_name.setText(intent.getStringExtra("name"))
        writer_time.setText(intent.getStringExtra("time"))

        adapter.setOnItemClickListener { item, view ->
            val comment = item as BBS_CommentItem
            Intent(this, UserProfileActivity::class.java).apply {
                putExtra("name", comment.username)
                putExtra("uid", comment.uid)
            }.run { startActivity(this) }
        }

    }
    private fun deleteDialog(bbs_tabname : String,bbs_oid: String){
        val dialog = CustomDialog_bbs_delete_check(this)
        val title="게시글 삭제"
        val description="게시글을 삭제하시겠습니까?"
        val action_text="삭제"
        dialog.mydialog(title,description,action_text)
        dialog.setOnclickedListener(object :
            CustomDialog_bbs_delete_check.ButtonClickListener {
            override fun onclickAction() {
                try {
                    trash(bbs_tabname,bbs_oid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onlcickClose() {

            }
        })
    }

    //게시물 지우기
    private fun trash(bbs_tabname : String,bbs_oid:String){
        db.collection(bbs_tabname).document(bbs_oid).delete().addOnSuccessListener {
            Log.d("yb","게시물이 삭제되었습니다.")
            val intent= Intent(this,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getWriterProfile() {

        // 게시글 작성자 프로필 사진 가져오기
        db.collection("users").document(intent.getStringExtra("uid").toString()).collection("userprofiles").document(intent.getStringExtra("uid").toString()).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<UserProfile>()
                Log.e("joo","uidddddd"+intent.getStringExtra("uid").toString())
                Glide.with(this).load(result?.profilePhoto).into(img_bbsCommon_writer)
            }
    }

    // 하트 Like 유무 확인
    private fun showHeart(uid : String, bbs_oid: String) {
        val heartPost = db.collection("users").document(uid).collection("HeartPost").document(bbs_oid).get()
        heartPost.addOnSuccessListener {
            //oid 존재하고 true면
            // 작성 해야됨
            if (it.exists()){
                heartFlag = true
                Log.e("joo", "showHeart: 색깔 하트로 변경 ")
                img_bbsCommon_writerLike.setImageResource(R.drawable.btn_pink_hearton)
            }else{
                heartFlag = false
                Log.e("joo", "showHeart: 빈 하트로 번경")
            }
            // 색깔 하트로 변경
        }.addOnFailureListener {

        }
    }

    // 댓글 출력 이벤트
    private fun getComment(bbs_tabname : String, bbs_oid: String) {

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

    }

    // 게시물 하트 누르기
    private fun heartClick(uid: String, bbs_oid: String, bbs_tabname: String) {
        if (heartFlag){
            db.collection("users").document(uid).collection("HeartPost").document(bbs_oid).delete()
                .addOnSuccessListener {
                    Log.d("joo", "좋아요 취소 성공 빈하트로 변경 ")
                    db.collection(bbs_tabname).document(bbs_oid).update("heartCnt", FieldValue.increment(-1))
                        .addOnSuccessListener {
                            Log.d("joo", "좋아요 취소 카운드 감소 성공")
                            refresh()
                        }
                        .addOnFailureListener { e -> Log.w("joo", "Error writing document1", e) }
                }.addOnFailureListener { e -> Log.w("joo", "Error 좋아요 취소 실패,", e)  }
        }else{
            val likePost = HeartPost(bbs_oid, true)
            //내가 하트 누른 게시물은 내 프로필에 등록돼야됨
            db.collection("users").document(uid).collection("HeartPost").document(bbs_oid).set(likePost)
                .addOnSuccessListener {
                    Log.d("joo", "Success 색깔 하트로 변경")
                    db.collection(bbs_tabname).document(bbs_oid).update("heartCnt", FieldValue.increment(1))
                        .addOnSuccessListener {
                            Log.d("joo", "좋아요 취소 카운드 감소 성공")
                            refresh()
                        }
                        .addOnFailureListener { e -> Log.w("joo", "Error writing document2", e) }
                }
                .addOnFailureListener { e -> Log.w("joo", "Error writing document3", e) }

        }
    }

    private fun writeComment(uid : String, bbs_tabname: String, bbs_oid: String) {

            db.collection("users").document(uid).collection("userprofiles").document(uid).get()
                .addOnSuccessListener { result ->
                    val result = result.toObject<UserProfile>()
                    Log.e("joo",result.toString())
                    commentnickname = result?.userName.toString()
                    commentProfile = result?.profilePhoto.toString()
                    Log.e("joo","nickname,, profile : "+commentProfile+commentnickname )
                    Log.e("joo", "tab:"+bbs_tabname)

                    postComment(bbs_tabname, bbs_oid, commentnickname, commentProfile)

                    try {
                        refresh()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

    }

    // 게시물 댓글 입력 메서드
    private fun postComment(bbs_tab : String, bbs_oid : String, nickname : String, profile : String){

        val uid = auth.uid.toString()
        val comment = edittext_bbscommon_comment.text.toString()
        val time = currenttime().toString()
        val bbscomment = BBS_Comment(uid, comment, nickname, time, profile)

        edittext_bbscommon_comment.setText("")

        val doc = db.collection(bbs_tab).document(bbs_oid).collection("Comment").document()
        Log.e("joo", "postComment id :"+ doc.id)
        doc.set(bbscomment)
    }

    private fun currenttime(): String? {

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
        val curTime = dateFormat.format(Date(time))

        return curTime
    }

    private fun refresh(){
        finish() //현재 액티비티 종료 실시
        overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
        startActivity(intent) //현재 액티비티 재실행 실시
        overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
    }
}