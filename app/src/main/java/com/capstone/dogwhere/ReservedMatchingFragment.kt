package com.capstone.dogwhere

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.dogwhere.DTO.Matching_Reserved_List_Item
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.Matching_InUsers
import com.capstone.dogwhere.DTO.Matching_List_Item
import com.capstone.dogwhere.DTO.Matching_Registered_List_Item

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_completed_matching.*
import kotlinx.android.synthetic.main.fragment_reserved_matching.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReservedMatchingFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_reserved_matching, container, false)

        init()


        return view
    }


    private fun init() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid
        val matchinglist = mutableListOf<String>()

        val matching = FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("matching")

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd")
        val curTime = dateFormat.format(Date(time))

        Log.d("currentTime", "currentTime : ${curTime}")

        matching.get().addOnSuccessListener {
            for (document in it) {
                Log.d("ReservedMatching", "documnet_Id4 = ${document.id}")
                Log.d("ReservedMatching", "documnet_Id5 = ${document["matchingLeaderUid"]}")
                if (!uid.equals(document["matchingLeaderUid"].toString())) {
                    matchinglist.add(document["documentId"].toString())
                }


            }
            if (!matchinglist.isEmpty()) {
                Log.d("ReservedMatching", "documnet_Id6 = ${matchinglist}")
                Log.d("ReservedMatching", "documnet_Id7 = ${matchinglist.component1()}")
                db.collection("Matching").whereIn("documentId", matchinglist)
                    .whereEqualTo("ongoing", true).orderBy("conversion_date", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener {
                        for (document in it) {
                            Log.d("ReservedMatching", "documnet_Id8 = ${document.id}")
                            adapter.add(
                                Matching_Reserved_List_Item(
                                    document.get("uid").toString(),
                                    document.id,
                                    document.get("title").toString(),
                                    document.get("date").toString() + "-" + document.get("startime")
                                        .toString(),
                                    document.get("place").toString(),
                                    document.get("date").toString(),
                                    document.get("startime").toString(),
                                    document.get("doneTime").toString()
                                )
                            )
                            recycler_reserved_matching?.adapter = adapter
                        }
                        adapter.setOnItemClickListener { item, view ->
                            Log.d("clickedmatching", (item as Matching_Reserved_List_Item).title)
                            val intent = Intent(context, MatchingDetailActivity::class.java)
                            intent.putExtra("documentId", (item).documentId)
                            intent.putExtra("leaderuid", (item).leaderUid)
                            startActivity(intent)
                        }
                    }
            } else {
                Log.d("yb", "yb matchingList ????????????")
                reservedMatching_text_empty.setText("????????? ?????? ??????")
            }


        }

        Log.d("ReservedMatching", "documnet_Id9 = ${matchinglist}")

    }

    private fun CalculatingTime(datetime: String): String {
        val time = System.currentTimeMillis()

        val dateFormat = SimpleDateFormat("yyyy/MM/dd/kk/mm")
        val curTime = dateFormat.format(Date(time))

        val formattedDateTime = datetime.replace("/", "")
        val formattedCurTime = curTime.replace("/", "")
        val total_time = ""

        return total_time
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}