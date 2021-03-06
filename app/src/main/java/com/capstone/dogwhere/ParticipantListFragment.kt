package com.capstone.dogwhere

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.Dog_Profile_Item
import com.capstone.dogwhere.DTO.Matching
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_party_list.*


class ParticipantListFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
//    var gridLayoutManager = GridLayoutManager(this.context, 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_party_list, container, false)
        init()


        return view
    }

    private fun init() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid

        val documentId =
            (activity as MatchingDetailActivity).intent.getStringExtra("documentId").toString()
        //다 가져오는게 아니라 새로만든 거 .get()해서 가져오기
        db.collection("Matching").document(documentId).collection("participant").get()
            .addOnSuccessListener {
                val it = it.toObjects<Matching>()
                for (document in it) {
                    db.collection("Matching").document(documentId).collection("participant")
                        .document(
                            document.uid
                        ).collection("dogprofile").get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                if (document != null) {
                                    adapter.add(
                                        Dog_Profile_Item(
                                            document["uid"].toString(),
                                            document["docid"].toString(),
                                            document["age"].toString() + "살",
                                            document["name"].toString(),
                                            document["breed"].toString(),
                                            document["sex"].toString(),
                                            document["neu"] as Boolean,
                                            document["img"].toString()
                                        )
                                    )
                                    Log.d(
                                        "DogProfile읽기 성공",
                                        "강아지 이름 :" + document["name"].toString()
                                    )
                                }
                                recyclerview_participant_list?.adapter = adapter
//                                recyclerview_participant_list.layoutManager = gridLayoutManager
                            }
                        }
                }

            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
