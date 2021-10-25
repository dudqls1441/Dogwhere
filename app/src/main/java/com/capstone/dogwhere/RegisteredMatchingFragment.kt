package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.Matching_Registered_List_Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_completed_matching.*
import kotlinx.android.synthetic.main.fragment_registered_matching.*
import kotlinx.android.synthetic.main.fragment_reserved_matching.*

class RegisteredMatchingFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_registered_matching, container, false)

        init()
        return view
    }

    private fun init() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid
        val matchinglist = mutableListOf<String>()

        //내가 등록한 매칭
        db.collection("users").document(uid).collection("matching").get().addOnSuccessListener {
            for (document in it) {
                if (uid.equals(document["matchingLeaderUid"].toString())) {
                    matchinglist.add(document["documentId"].toString())
                }
            }
            Log.d("yb","ybyb matchingList -> ${matchinglist}")
            if(!matchinglist.isEmpty()){
                //.whereEqualTo("ongoing",false)
                db.collection("Matching").whereIn("documentId", matchinglist).get()
                    .addOnSuccessListener {
                        for (document in it) {
                            Log.d("Registerd", "Registerd:documnet_Id8 = ${document.id}")
                            adapter.add(
                                Matching_Registered_List_Item(
                                    document.id,
                                    document.get("title").toString(),
                                    document.get("date").toString() + "-" + document.get("startime")
                                        .toString(),
                                    document.get("place").toString()
                                )

                            )
                            recycler_registered_matching?.adapter = adapter
                        }
                        adapter.setOnItemClickListener { item, view ->
                            val intent = Intent(activity,MatchingDetailActivity::class.java)
                            intent.putExtra("documentId",(item as Matching_Registered_List_Item).documentId)
                            startActivity(intent)


                        }
                    }
            }else{
                Log.d("yb","yb matchingList 비어있음")
                registeredMatching_text_empty.setText("등록한 매칭 없음")
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}