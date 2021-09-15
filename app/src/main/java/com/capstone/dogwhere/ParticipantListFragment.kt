package com.capstone.dogwhere

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_party_list.*

class ParticipantListFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var participant_profile_list : participant_profile_list
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

        val participant : participant_profile_list
        db.collection("users").document(uid).collection("userprofiles").document(uid).get().addOnSuccessListener {
            participant_profile_list!!.uid  = it["username"].toString()
        }



//        { result ->
//
//            Log.d("ParticipantListFragment","users -> uid -> 정보 : ${result.data} //  matadata : ${result.metadata}  //  exists (Boolean): ${result.exists()}")
//
//
//        }


//        db.collection("Matching").document(documentId).collection("participant").get()
//            .addOnSuccessListener { it ->
//                Log.d("PartyListFragment", "PartyListFragment있음")
//                for (document in it) {
//                    Log.d("PartyListFragment", "PartyListFragment : ${document.data}")
//                    val uid = document.get("uid").toString()
//                    db.collection("users").document(uid!!).collection("userprofiles").document(uid)
//                        .get()
//                        .addOnSuccessListener { result1 ->
//                            db.collection("users").document(uid!!).collection("dserprofiles")
//                                .document(uid!!).get()
//                                .addOnSuccessListener { result2 ->
//                                    val result1 = result1.toObject<UserProfile>()
//                                    val result2 = result2.toObject<DogProfile>()
//                                    Log.d("ParticipantListFragment","ParticipantListFragment : ${result1} ,,,,${result2}")
//                                    adapter.add(
//                                        participant_profile_list(
//                                            uid,
//                                            result1?.userName.toString(),
//                                            "주소 모름",
//                                            result1?.profilePhoto.toString(),
//                                            result2?.dogName.toString(),
//                                            result2?.dogAge.toString(),
//                                            result2?.dogBreed.toString(),
//                                            result2?.photoUrl.toString()
//                                        )
//                                    )
//                                    recyclerview_participant_list.adapter = adapter
//                                }
//
//                        }
//                }
//            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}

//        db.collection("Matching").document(documentId).collection("participant").get().addOnSuccessListener { result1 ->
//            Log.d("PartyListFragment", "PartyListFragment있음")
//            for(document in result1){
//                Log.d("PartyListFragment", "PartyListFragment : ${document.data}")
//                val uid = document.get("uid").toString()
//                db.collection("users").document(uid).collection("userprofiles").get().addOnSuccessListener { result2 ->
//                    val result1 =result1.toObjects<UserProfile>()
//                    val result2 =result2.toObjects<DogProfile>()
//                    adapter.add(
//                        participant_profile_list(
//                            uid,
//                            result1!!.usernmae.toString
//
//                        )
//                    )
//
//
//
//                }
//
//            }
//        }

