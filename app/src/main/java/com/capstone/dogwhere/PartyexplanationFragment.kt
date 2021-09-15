package com.capstone.dogwhere

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.Matching
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class PartyexplanationFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_party_explanation, container, false)
        val text_explain = view?.findViewById<TextView>(R.id.text_matching_explain)

        if ((activity as MatchingDetailActivity).intent.hasExtra(
                "documentId"
            )
        ) {
            val documentId =
                (activity as MatchingDetailActivity).intent.getStringExtra("documentId").toString()
            val db = FirebaseFirestore.getInstance()
            db.collection("Matching").document(documentId).get().addOnSuccessListener {
                val result = it.toObject<Matching>()
                text_explain!!.setText(result!!.explanation)
            }.addOnFailureListener {
                Log.e("getdb", "getdb실패")
            }
        } else {
            Log.e("matchingexplain", "matchingexplain -> uid 가져오기 실패")
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}