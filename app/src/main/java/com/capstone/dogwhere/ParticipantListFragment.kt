package com.capstone.dogwhere

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_dog_profile.*
import kotlinx.android.synthetic.main.activity_search_region.*
import kotlinx.android.synthetic.main.fragment_party_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class ParticipantListFragment : Fragment() {
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

                db.collection("Matching").document(documentId).collection("participant").get()
                    .addOnSuccessListener {
                        val it = it.toObjects<Matching>()
                        for (document in it) {
                            db.collection("users").document(document.uid).collection("dogprofiles")
                               .get()
                                .addOnSuccessListener { result ->
                                    for (document in result) {
                                        val dogs = document.toObject<DogProfile>()
                                        if (dogs != null) {
                                            Log.d("dogprofile있음", result.toString())
                                            adapter.add(
                                                Dog_Profile_Item(
                                                    dogs.uid,
                                                    dogs?.dogAge + "살",
                                                    dogs?.dogName,
                                                    dogs?.dogBreed,
                                                    dogs?.dogSex,
                                                    true,
                                                    dogs?.photoUrl.toString()
                                                )
                                            )
                                            Log.d("DogProfile읽기 성공", "강아지 이름 : ${dogs?.dogName}")
                                        }
                                        recyclerview_participant_list?.adapter = adapter
                                    }
                                }
                        }

                    }
        }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
