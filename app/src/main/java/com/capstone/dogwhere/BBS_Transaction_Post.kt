package com.capstone.dogwhere

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.dogwhere.BBS.HeartPost
import com.capstone.dogwhere.Chat.ChatRoomActivity
import com.capstone.dogwhere.DTO.BBS_Comment
import com.capstone.dogwhere.DTO.BBS_CommentItem
import com.capstone.dogwhere.DTO.UserProfile
import com.capstone.dogwhere.FCM.MyReceiver
import com.firepush.Fire
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__post.*
import kotlinx.android.synthetic.main.activity_b_b_s__transaction__post.img_product
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.text.SimpleDateFormat
import java.util.*

class BBS_Transaction_Post : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var commentnickname : String
    private lateinit var commentProfile : String
    val adapter = GroupAdapter<GroupieViewHolder>()
    var heartFlag : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {

        val bbs_oid = intent.getStringExtra("oid").toString()
        val bbs_tabname = intent.getStringExtra("tab").toString()
        Fire.init("AAAA1P59Tgs:APA91bEuZ_Hp7rsbkRmR0zWrI_uDhd9o3RMXz4oBpOeXHGc_RCJEo_-d1J-_BL5Hl4jk0KmzjZmWzzNeCOJ4n8jsiFo53QNaknXCq4fOwvbkuSpXNF08XMYud8dY8fHPl1PDMj8-_EDU")

        Log.e("joo", "bbs_oid, bbs_tabname - " + bbs_oid + bbs_tabname )
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid.toString()
        val your_uid = intent.getStringExtra("uid").toString()
        val name = intent.getStringExtra("name").toString()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s__transaction__post)



        //????????? ??????
        setProductPhoto(bbs_oid)


        //DB????????? ??? ??????
//        changedDocument(bbs_tabname,your_uid)
        Log.d("ybyb","bbs_tabname - >${bbs_tabname}")
        Log.d("ybyb","writerUid - >${your_uid}")

        if(uid.equals(your_uid)||your_uid =="null"){
            btn_bbsTrans_trash.visibility= View.VISIBLE
            btn_bbsTrans_trash.setOnClickListener {
                Log.d("ybyb","myuid-> ${uid} your_uid-> ${your_uid}")
                if(uid!!.equals(your_uid)){
                    deleteDialog(bbs_oid)
                    Log.d("ybyb","ybyb??? ?????????")
                }else{
                    Log.d("ybyb","ybyb??? ????????? ??????")
                }
            }
        }



        // ????????? ????????? ?????????
        getWriterProfile()
        // Like ?????? ??????
        showHeart(uid, bbs_oid)
        // ?????? ?????? ?????????
        getComment(bbs_tabname, bbs_oid)
        // ?????? ?????? ?????????
        btn_bbsTrans_send.setOnClickListener {
            if(edittext_bbsTrans_comment.text.toString() != ""){
                writeComment(your_uid, bbs_tabname, bbs_oid)
            }
        }
        // ????????? ?????? ?????? ?????????
        img_bbsTrans_writerLike.setOnClickListener{
            heartClick(uid, bbs_oid, bbs_tabname)
        }

        img_bbsTrans_writerChat.setOnClickListener {
            val bbs_uid = intent.getStringExtra("uid").toString()
            val bbs_name = intent.getStringExtra("name").toString()
            val intent = Intent(this, ChatRoomActivity::class.java)
            Log.e("joo", "yourUid:"+bbs_uid+"  name:"+bbs_name)
            intent.putExtra("yourUid", bbs_uid)
            intent.putExtra("name", bbs_name)
            startActivity(intent)
        }

        img_bbsTrans_writer.setOnClickListener {
            Intent(this, UserProfileActivity::class.java).apply {
                putExtra("name", name)
                putExtra("uid", your_uid)
            }.run { startActivity(this) }
        }

        text_bbsTrans_postTitle.setText(intent.getStringExtra("title"))
        text_bbsTrans_postContent.setText(intent.getStringExtra("content"))
        text_bbsTrans_writerName.setText(intent.getStringExtra("name"))
        text_bbsTrans_writerTime.setText(intent.getStringExtra("time"))
        text_bbsTrans_price.setText("\\" + intent.getStringExtra("price"))

        btn_bbsTrans_back.setOnClickListener {
            finish()
        }

        adapter.setOnItemClickListener { item, view ->
            val comment = item as BBS_CommentItem
            Log.e("joo", comment.username+comment.uid)
            Intent(this, UserProfileActivity::class.java).apply {
                putExtra("name", comment.username)
                putExtra("uid", comment.uid)
            }.run { startActivity(this) }
        }

    }
    private fun deleteDialog(bbs_oid: String){
        val dialog = CustomDialog_bbs_delete_check(this)
        val title="????????? ??????"
        val description="???????????? ?????????????????????????"
        val action_text="??????"
        dialog.mydialog(title,description,action_text)
        dialog.setOnclickedListener(object :
            CustomDialog_bbs_delete_check.ButtonClickListener {
            override fun onclickAction() {
                try {
                    trash(bbs_oid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onlcickClose() {

            }
        })
    }

    //????????? ?????????
    private fun trash(bbs_oid:String){
        db.collection("transaction_bbs").document(bbs_oid).delete().addOnSuccessListener {
            Log.d("yb","???????????? ?????????????????????.")
            val intent= Intent(this,MainMenuActivity::class.java)
            intent.putExtra("state", intent.getStringExtra("tab").toString())
            startActivity(intent)
            finish()
        }
    }
    private fun setProductPhoto(bbs_oid: String) {
        Log.d("yb","bbs_oid -> ${bbs_oid}")
        db.collection("transaction_bbs").document(bbs_oid).get().addOnSuccessListener {
            val img = it.get("uri").toString()
            Log.d("yb","img ->${img}")
            Log.d("yb","img -> ${img}")
            img_product.visibility= View.VISIBLE
            Glide.with(this).load(img).placeholder(R.drawable.backgroundgray)
                .apply(RequestOptions()).into(img_product)
        }

    }

    // ????????? ?????? ?????????
    private fun heartClick(uid: String, bbs_oid: String, bbs_tabname: String) {
        if (heartFlag){
            db.collection("users").document(uid).collection("HeartPost").document(bbs_oid).delete()
                .addOnSuccessListener {
                    Log.d("joo", "????????? ?????? ?????? ???????????? ?????? ")
                    db.collection(bbs_tabname).document(bbs_oid).update("heartCnt", FieldValue.increment(-1))
                        .addOnSuccessListener {
                            Log.d("joo", "????????? ?????? ????????? ?????? ??????")
                            refresh()
                        }
                        .addOnFailureListener { e -> Log.w("joo", "Error writing document1", e) }
                }.addOnFailureListener { e -> Log.w("joo", "Error ????????? ?????? ??????,", e)  }
        }else{
            val likePost = HeartPost(bbs_oid, true)
            //?????? ?????? ?????? ???????????? ??? ???????????? ???????????????
            db.collection("users").document(uid).collection("HeartPost").document(bbs_oid).set(likePost)
                .addOnSuccessListener {
                    Log.d("joo", "Success ?????? ????????? ??????")
                    db.collection(bbs_tabname).document(bbs_oid).update("heartCnt", FieldValue.increment(1))
                        .addOnSuccessListener {
                            Log.d("joo", "????????? ?????? ????????? ?????? ??????")
                            refresh()
                        }
                        .addOnFailureListener { e -> Log.w("joo", "Error writing document2", e) }
                }
                .addOnFailureListener { e -> Log.w("joo", "Error writing document3", e) }

        }
    }

    // ?????? Like ?????? ??????
    private fun showHeart(uid : String, bbs_oid: String) {
        val heartPost = db.collection("users").document(uid).collection("HeartPost").document(bbs_oid).get()
        heartPost.addOnSuccessListener {
             //oid ???????????? true???
            // ?????? ?????????
            if (it.exists()){
                heartFlag = true
                Log.e("joo", "showHeart: ?????? ????????? ?????? ")
                img_bbsTrans_writerLike.setImageResource(R.drawable.btn_pink_hearton)
            }else{
                heartFlag = false
                Log.e("joo", "showHeart: ??? ????????? ??????")
            }
            // ?????? ????????? ??????
        }.addOnFailureListener {

        }
    }


    private fun writeComment(writerUid: String, bbs_tabname: String, bbs_oid: String) {
        val uid = auth.uid.toString()
            db.collection("users").document(uid).collection("userprofiles").document(uid).get()
                .addOnSuccessListener { result ->
                    val result = result.toObject<UserProfile>()
                    Log.e("joo",result.toString())
                    commentnickname = result?.userName.toString()
                    commentProfile = result?.profilePhoto.toString()
                    Log.e("joo","nickname,, profile : "+commentProfile+commentnickname )
                    Log.e("joo", "tab:"+bbs_tabname)

                    postComment(writerUid,bbs_tabname, bbs_oid, commentnickname, commentProfile)

                    try {
                        refresh()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

    }


    private fun getWriterProfile() {

        // ????????? ????????? ????????? ?????? ????????????
        db.collection("users").document(intent.getStringExtra("uid").toString()).collection("userprofiles").document(intent.getStringExtra("uid").toString()).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<UserProfile>()
                Log.e("joo","uidddddd"+intent.getStringExtra("uid").toString())
                Glide.with(this).load(result?.profilePhoto).into(img_bbsTrans_writer)
            }
    }

    private fun getComment(bbs_tabname : String, bbs_oid: String) {

        // ?????? ??????
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
                recycler_bbsTrans_comment?.adapter = adapter
            }
    }

    // ????????? ?????? ?????? ?????????
    private fun postComment(writerUid:String,bbs_tab : String, bbs_oid : String, nickname : String, profile : String){

        val uid = auth.uid.toString()
        val comment = edittext_bbsTrans_comment.text.toString()
        val time = currenttime().toString()
        val bbscomment = BBS_Comment(uid, comment, nickname, time, profile)

        edittext_bbsTrans_comment.setText("")

        val doc = db.collection(bbs_tab).document(bbs_oid).collection("Comment").document()
        doc.set(bbscomment).addOnSuccessListener {
            db.collection(bbs_tab).document(bbs_oid).get().addOnSuccessListener {
                val PostTile = it.get("title").toString()
                db.collection("users").document(writerUid).collection("userprofiles").document(writerUid).get().addOnSuccessListener {
                    val userToken = it.get("userToken").toString()
                    Log.d("ybyb","????????? userToken ->${userToken}")
                    send_fcm(
                        PostTile+ " ???????????? ????????? ???????????????.",
                        comment,
                        userToken
                    )
                }
            }
        }
    }

    private fun currenttime(): String? {

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
        val curTime = dateFormat.format(Date(time))

        return curTime
    }

    private fun refresh(){
        finish() //?????? ???????????? ?????? ??????
        overridePendingTransition(0, 0) //????????? ??????????????? ?????????
        startActivity(intent) //?????? ???????????? ????????? ??????
        overridePendingTransition(0, 0) //????????? ??????????????? ?????????
    }

    private fun send_fcm(title: String, content: String, receiverToken: String) {
        Fire.create()
            .setTitle(title)
            .setBody(content)
            .setCallback { pushCallback, exception ->
                Log.d("ybyb", "push->${pushCallback}")
                Log.d("ybyb", "e->${exception.toString()}")
                Log.d("ybyb", "???????????????: ${pushCallback.isSent}")

            }
            .toIds(receiverToken)  //toTopic("FOR TOPIC") or toCondition("CONDITION HERE")
            .push()
    }

}