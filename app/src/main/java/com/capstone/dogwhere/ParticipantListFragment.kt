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
import com.google.firebase.firestore.ktx.toObjects
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
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
    lateinit var part:Array<participant_profile_list>
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

        var list = arrayListOf<String>()

        suspend fun dog(): Boolean {
            return try {

                db.collection("Matching").document(documentId).collection("participant").get()
                    .addOnSuccessListener {
                        val it = it.toObjects<Matching>()
                        for (document in it) {
                            list.add(document.uid)
                        }
                        part = Array(list.size,{participant_profile_list("", "", "", "", "", "", "", "")})
                        Log.e("yy",  list.toString() + "!!")
                        Log.e("yy",   list.size.toString() + "!!")

                    }
                    .await()
                true
            } catch (e: FirebaseException) {
                false
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            runBlocking {
                dog()
            }
            var test = 0
            for (documents in list) {
                db.collection("users").document(documents).collection("userprofiles")
                    .document(documents).get()
                    .addOnSuccessListener {
                        part[test].username = it["userName"].toString()
                        part[test].userimg = it["profilePhoto"].toString()
                        Log.e("yy", part[test].username + "_2" + test)
                        test++

                    }
            }
            var testt=0
            for (document in list) {
                db.collection("users").document(document).collection("dogprofiles")
                    .document(document).get()
                    .addOnSuccessListener { result ->
                        part[testt].useraddress = "주소값"
                        part[testt].uid = document
                        part[testt].dogimg = result["photoUrl"].toString()
                        part[testt].dogname = result["dogName"].toString()
                        part[testt].dogage = result["dogAge"].toString()
                        part[testt].dogbreed = result["dogBreed"].toString()
                        Log.e("yy", part[testt].dogname + "_1"+testt)
                        adapter.add(part[testt])
                        testt++
                    }
            }
                    getActivity()?.runOnUiThread(Runnable {
                        recyclerview_participant_list?.adapter = adapter
                        // Stuff that updates the UI
                    })

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}
