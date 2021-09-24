package com.capstone.dogwhere

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
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
        var useruid: String

        var part = arrayListOf<participant_profile_list>()
        var list = arrayListOf<String>()


        suspend fun dog(): Boolean {
            return try {
                db.collection("Matching").document(documentId).collection("participant").get()
                    .addOnSuccessListener {
                        val it = it.toObjects<Matching>()
                        var test = 0
                        for (document in it) {
                            Log.e("yy", document.uid.toString() + "!!")

                            db.collection("users").document(document.uid).collection("dogprofiles")
                                .document(document.uid).get()
                                .addOnSuccessListener { result ->
                                    var participantProfileList =
                                        participant_profile_list("", "", "", "", "", "", "", "")
                                    part.add(test, participantProfileList)
                                    part[test].useraddress = "주소값"
                                    part[test].uid = document.uid
                                    part[test].dogimg = result["photoUrl"].toString()
                                    part[test].dogname = result["dogName"].toString()
                                    part[test].dogage = result["dogAge"].toString()
                                    part[test].dogbreed = result["dogBreed"].toString()
                                    list.add(part[test].uid)
                                    Log.e("yy", list.toString())
                                    Log.e("yy", part[test].dogname + "_1")
                                    test++
                                }
                        }

                    }.await()
                true
            } catch (e: FirebaseException) {
                false
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            runBlocking {
                dog()
            }
            db.collection("Matching").document(documentId).collection("participant").get()
                .addOnSuccessListener {
                    val it = it.toObjects<Matching>()
                    var test = 0
                    for (document in list) {
                        db.collection("users").document(document).collection("userprofiles")
                            .document(document).get()
                            .addOnSuccessListener {
                                part[test].username = it["userName"].toString()
                                part[test].userimg = it["profilePhoto"].toString()
                                Log.e("yy", part[test].username + "_2")
                                adapter.add(part[test])
                                test++
                            }
                    }

                    getActivity()?.runOnUiThread(Runnable {
                        recyclerview_participant_list?.adapter = adapter
                        // Stuff that updates the UI
                    })

                }
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
