package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.Matching_Completed_List_Item
import com.capstone.dogwhere.DTO.Matching_Reserved_List_Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_completed_matching.*
import kotlinx.android.synthetic.main.fragment_reserved_matching.*

class CompletedMatchingFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_completed_matching, container, false)

        init()
        return view
    }

    private fun init() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid
        val matchinglist = mutableListOf<String>()

        db.collection("users").document(uid).collection("matching").get().addOnSuccessListener {
//            for (document in it) {
//                if (!uid.equals(document["matchingLeaderUid"].toString())) {
//                    matchinglist.add(document["documentId"].toString())
//                }
//            }
            for (document in it) {
                    matchinglist.add(document["documentId"].toString())
            }
            if(!matchinglist.isEmpty()){
                //.whereEqualTo("ongoing",false)
                db.collection("Matching").whereIn("documentId", matchinglist).whereEqualTo("ongoing",false).get()
                    .addOnSuccessListener {
                        for (document in it) {
                            Log.d("CompletedMatching", "Completed:documnet_Id8 = ${document.id}")
                            adapter.add(
                                Matching_Completed_List_Item(
                                    document.get("uid").toString(),
                                    document.id,
                                    document.get("title").toString(),
                                    document.get("date").toString() + "-" + document.get("startime")
                                        .toString(),
                                    document.get("place").toString()
                                )

                            )
                            recycler_completed_matching?.adapter = adapter
                        }
                        adapter.setOnItemClickListener { item, view ->
                            val intent = Intent(activity,MatchingDetailActivity::class.java)
                            intent.putExtra("documentId",(item as Matching_Completed_List_Item).documentId)
                            intent.putExtra("preActivity", "CompletedMatchingFragment")
                            intent.putExtra("title", item.time)
                            intent.putExtra("documentId", item.documentId)
                            intent.putExtra("leaderuid", uid)
                            startActivity(intent)
                        }
                    }
            }else{
                Log.d("yb","yb matchingList ????????????")
                completedMatching_text_empty.setText("????????? ?????? ??????")
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}