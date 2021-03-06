package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.DTO.DogProfile
import com.capstone.dogwhere.DTO.Dog_Profile_Item
import com.capstone.dogwhere.DTO.Matching_Completed_List_Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_user_profile.*

class UserProfileFragment(profile_uid : String) : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>

    val profile_uid : String = profile_uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid
        adapter = GroupAdapter<GroupieViewHolder>()
        db.collection("users").document(profile_uid).collection("dogprofiles").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val dogs = document.toObject<DogProfile>()
                    if (dogs != null) {
                        Log.d("dogprofile있음", result.toString())
                        adapter.add(
                            Dog_Profile_Item(
                                dogs.uid,
                                document.id,
                                dogs?.dogAge + "살",
                                dogs?.dogName,
                                dogs?.dogBreed,
                                dogs?.dogSex,
                                dogs?.neutering,
                                dogs?.photoUrl.toString()
                            )
                        )
                        Log.d("DogProfile읽기 성공", "강아지 이름 : ${dogs?.dogName}")
                    }
                    dog_recyclerview?.adapter = adapter
                }
                adapter.setOnItemClickListener { item, view ->
                        if(profile_uid.equals(uid)){
                        val intent = Intent(activity,Modify_dog_profile::class.java)
                        intent.putExtra("docid", (item as Dog_Profile_Item).docId)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}