package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_matching_list.*
import kotlinx.android.synthetic.main.activity_matching_list.btn_back
import kotlinx.android.synthetic.main.activity_matching_registration.*
import kotlinx.android.synthetic.main.navi_header.*

class MatchingListActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var condition: String
    private lateinit var adapter: GroupAdapter<com.xwray.groupie.GroupieViewHolder>
    private var condition_size = "all"
    private var condition_neutralization = "all"
    private var condition_owner_gender = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_list)
        Log.d("yy", intent.getStringExtra("address").toString())
        btn_back.setOnClickListener {
            finish()
        }
        adapter = GroupAdapter<GroupieViewHolder>()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        radio_condition()
        radio_condition_size()
        radio_condition_neutralization()
        radio_condition_gender()
        btn_condition_search.setOnClickListener {
            Log.d(
                "yb",
                "yb check -> ${condition_size},,,${condition_neutralization},,,${condition_owner_gender}"
            )
            checked_2(
                condition_size,
                condition_neutralization,
                condition_owner_gender
            )

        }

        adapter.setOnItemClickListener { item, view ->
            Log.d(
                "ClickMatching",
                (item as Matching_List_Item).title
            )
            Intent(
                this,
                MatchingDetailActivity::class.java
            ).apply {
                putExtra("title", (item).title)
                putExtra("leaderuid", (item).uid)
                putExtra("documentId", (item).documentId)
                putExtra("preActivity", "MatchingListActivity")
            }.run {
                startActivity(this)
            }
        }

    }


    private fun radio_condition(
    ) {
        checked_1()
        layout_choice_condition.visibility = View.GONE
        select_condition_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.search_all -> let {
                    checked_1()
                    layout_choice_condition.visibility = View.GONE
                }
                R.id.search_condition -> let {
//                    checked_1()
                    layout_choice_condition.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun radio_condition_size() {
        condition_group_size.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.size_all -> condition_size = "all"
                R.id.size_small -> condition_size = "small"
                R.id.size_middle -> condition_size = "middle"
                R.id.size_big -> condition_size = "big"
            }
        }
    }


    private fun radio_condition_neutralization() {
        condition_group_neutralization.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.neutralization_all -> condition_neutralization = "all"
                R.id.neutralization -> condition_neutralization = "neutralization"

            }
        }
    }

    private fun radio_condition_gender() {
        condition_group_gender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.gender_all -> condition_owner_gender = "all"
                R.id.gender_man -> condition_owner_gender = "man"
                R.id.gender_woman -> condition_owner_gender = "wm"
            }
        }
    }

    private fun full(document: QueryDocumentSnapshot) {
        val uid = document.get("uid").toString()
        val documentId = document.id
        db.collection("users").document(uid!!).collection("userprofiles")
            .document(uid)
            .get()
            .addOnSuccessListener { result ->
                val result =
                    result.toObject<UserProfile>()
                adapter.add(
                    Matching_List_Item(
                        document.get("uid").toString(),
                        document.get("title").toString(),
                        document.get("date")
                            .toString() + "/" + document.get("startime"),
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

    private fun checked_1() {
        adapter.clear()
        db.collection("Matching").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("place").toString()
                            .contains(intent.getStringExtra("address").toString())
                    ) {
                        Log.e(
                            "yy",
                            document.get("place").toString() + " 있음"
                        ) //조건검색(지역)할때 사용
                        Log.d("Matching", "Matching있음")
                        Log.d("Matching", document.get("title").toString())
                        full(document)
                    } else if (intent.getStringExtra("address").toString() == "전체") {
                        full(document)
                    }
                }
            }.addOnFailureListener {
                Log.w("데이터베이스읽기실패", "Error getting document", it)
            }
    }

    private fun checked_2(
        condition_size: String,
        condition_neutralization: String,
        condition_owner_gender: String
    ) {
        adapter.clear()
//        if (condition_group_size.checkedRadioButtonId.toString()=="모두"&&condition_group_neutralization.checkedRadioButtonId.toString()=="모두"
//            &&condition_group_gender.checkedRadioButtonId.toString()=="모두"){
//        }
        db.collection("Matching").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("place").toString()
                            .contains(intent.getStringExtra("address").toString())
                    ) {
                        if(document.get("condition_dog_size").toString().contains(condition_size)
                            && document.get("condition_dog_neutralization").toString().contains(condition_neutralization)
                            && document.get("condition_owner_gender").toString().contains(condition_owner_gender)){
                            full(document)
                        }
                    } else if (intent.getStringExtra("address").toString() == "전체") {
                        if(document.get("condition_dog_size").toString().contains(condition_size)
                            && document.get("condition_dog_neutralization").toString().contains(condition_neutralization)
                            && document.get("condition_owner_gender").toString().contains(condition_owner_gender)){
                            full(document)
                        }
                    }
                }

            }

    }
}
