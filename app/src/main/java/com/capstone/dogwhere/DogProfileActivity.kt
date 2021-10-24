package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.DogProfile
import com.capstone.dogwhere.DTO.Dog_Profile_Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_dog_profile.*

class DogProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private val TAG = DogProfileActivity::class.java.simpleName
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog_profile)


        init()
        btn_back.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_add_dog.setOnClickListener {
            val intent = Intent(this, RegisterDogProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_upload.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }


//        val uid = auth.currentUser!!.uid
//
//
//        val postLef = rdb.getReference().child("userprofiles")
//
//        db.collection("users").document(uid).collection("userprofiles").document(uid).get()
//            .addOnSuccessListener { result ->
//                val result = result.toObject<UserProfile>()
//                Log.e("joo",result.toString())
//                Glide.with(this).load(result?.profilePhoto).circleCrop().into(user_photo_img)
//                user_name_text.setText(result?.userName)
//            }

    }

    private fun init() {
        val db = FirebaseFirestore.getInstance()
        adapter = GroupAdapter<GroupieViewHolder>()
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid

        Log.d(TAG, "현재 아이디 uid 값 : ${uid}")

        db.collection("users").document(uid).collection("dogprofiles").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val dogs = document.toObject<DogProfile>()
                    if(dogs != null){
                        Log.d("dogprofile있음", result.toString())
                        adapter.add(
                            Dog_Profile_Item(
                                dogs.uid,
                                document.id,
                                dogs?.dogAge + "살",
                                dogs?.dogName,
                                dogs?.dogBreed,
                                dogs?.dogSex,
                                dogs?.photoUrl.toString()
                            )
                        )
                        Log.d("DogProfile읽기 성공", "강아지 이름 : ${dogs?.dogName}")
                    }
                    dog_profiles_recyclerview?.adapter=adapter
                }
            }
//        db.collection("users").document(uid).collection("dogprofiles").document(uid).get()
//            .addOnSuccessListener { results ->
//                val result = results.toObject<DogProfile>()
//                if (result != null) {
//                    Log.d("dogprofile있음", result.toString())
//                    adapter.add(
//                        Dog_Profile_Item(
//                            result?.dogAge + "살",
//                            result?.dogName,
//                            result?.dogBreed,
//                            result?.dogSex,
//                            true,
//                            result?.photoUrl.toString()
//                        )
//                    )
//                    Log.d("DogProfile읽기 성공", "강아지 이름 : ${result.dogName}")
//                }
//                dog_profiles_recyclerview?.adapter=adapter
//            }
//
//
//            .addOnFailureListener { e ->
//                Log.d(TAG,"ERROE ${e.toString()}")
//            }
    }
}

//    private fun init() {
//        auth = FirebaseAuth.getInstance()
//        rdb = FirebaseDatabase.getInstance()
//        val uid = auth.currentUser!!.uid
//
//        db.collection("users").document(uid).collection("dogprofiles").document(uid).get()
//            .addOnSuccessListener { results ->
//                val result = results.toObject<DogProfile>()
//                if (result != null) {
//                    Log.d("dogprofile있음", result.toString())
//                    sub_RelativeLayout.visibility = View.GONE
//                    register_dog_frame_exist.visibility = View.VISIBLE
////                    val i :Int = 3
////                    val packName :String = this.packageName
////                    val dogN=resources.getIdentifier("dog_name"+i,"drawable",packName)
//
//                    if (result?.photoUrl != null) {
//                        Glide.with(this).load(result!!.photoUrl).into(img_dog_profile)
//                    } else {
//                        Log.d("no_img", "DogProfile에 이미지 없음")
//                    }
//                    dog_name.setText(result?.dogName)
//                    dog_breed.setText(result?.dogBreed)
//                    dog_age.setText(result?.dogAge +"살")
//                    dog_sex.setText(result?.dogSex)
//                    register_dog_frame_none2.visibility = View.VISIBLE
//                } else {
//                    Log.d("no_dog", "DogProfile없음")
//                }
//
//            }
//    }



