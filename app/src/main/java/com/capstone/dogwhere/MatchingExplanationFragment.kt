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
import kotlinx.android.synthetic.main.fragment_party_explanation.*

class MatchingExplanationFragment : Fragment() {
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
                var size=result!!.condition_dog_size
                when(size){
                    "all" -> condition_size.setText("제한 없음")
                    "all,small" -> condition_size.setText("소형견")
                    "all,middle" -> condition_size.setText("중형견")
                    "all,big" -> condition_size.setText("대형견")
                }
                var neu=result!!.condition_dog_neutralization
                when(neu){
                    "all" ->  condition_neutralization.setText("제한 없음")
                    "all,neutralization" -> condition_neutralization.setText("중성화 필수")
                }
                var gen=result!!.condition_owner_gender
                when(gen){
                    "all" ->  condition_gender!!.setText("제한 없음")
                    "all,man" ->  condition_gender!!.setText("남자")
                    "all,wm" ->  condition_gender!!.setText("여자")
                }


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