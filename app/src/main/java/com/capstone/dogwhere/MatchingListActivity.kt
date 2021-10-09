package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_matching_list.*
import kotlinx.android.synthetic.main.navi_header.*

class MatchingListActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_list)
        Log.d("yy",intent.getStringExtra("address").toString())
        btn_back.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        init()

    }

    private fun init() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        db.collection("Matching").get()
            .addOnSuccessListener { result ->
            for (document in result) {
                if(document.get("place").toString().contains(intent.getStringExtra("address").toString())){
                    Log.e("yy",document.get("place").toString()+" 있음") //조건검색(지역)할때 사용
                }
                Log.d("Matching", "Matching있음")
                Log.d("Matching", document.get("title").toString())
                val uid = document.get("uid").toString()
                val documentId = document.id
                db.collection("users").document(uid!!).collection("userprofiles").document(uid)
                    .get()
                    .addOnSuccessListener { result ->
                        val result = result.toObject<UserProfile>()
                        adapter.add(
                            Matching_List_Item(
                                document.get("uid").toString(),
                                document.get("title").toString(),
                                document.get("date").toString() + "/" + document.get("startime"),
                                document.get("place")
                                    .toString() + "/" + document.get("place_detail")
                                    .toString(),
                                result?.profilePhoto.toString(),
                                documentId
                            )

                        )
                        recyclerView_matching_list?.adapter = adapter
                    }
            }
        }.addOnFailureListener {
            Log.w("데이터베이스읽기실패", "Error getting document", it)
        }
        adapter.setOnItemClickListener { item, view ->
            Log.d("ClickMatching", (item as Matching_List_Item).title)
            Intent(this, MatchingDetailActivity::class.java).apply {
                putExtra("title", (item).title)
                putExtra("leaderuid", (item).uid)
                putExtra("documentId", (item).documentId)
                putExtra("preActivity","MatchingListActivity")
            }.run {
                startActivity(this)
            }
        }
    }
}