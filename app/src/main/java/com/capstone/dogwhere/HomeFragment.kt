package com.capstone.dogwhere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capstone.dogwhere.Chat.ChatListActivity
import com.capstone.dogwhere.DTO.UserProfile
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.navi_header.*

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    val db = Firebase.firestore
    companion object{
        fun newInstance():HomeFragment{
            return HomeFragment()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_home, container, false)
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance()
        val uid = auth.currentUser.uid


        val postLef = rdb.getReference().child("userprofiles")

        db.collection("users").document(uid).collection("userprofiles").document(uid).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<UserProfile>()
                Log.e("joo",result.toString())
                Glide.with(this).load(result?.profilePhoto).circleCrop().into(user_photo_img)
                user_name_text.setText(result?.userName)
            }

        return  view
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.matching_list -> chattingstart()
            R.id.choker_buy -> Toast.makeText(activity, "rwerqwer", Toast.LENGTH_SHORT).show()
            R.id.menu_list -> Toast.makeText(activity, "rwerqwer", Toast.LENGTH_SHORT).show()
            R.id.notice -> Toast.makeText(activity, "rwerqwer", Toast.LENGTH_SHORT).show()
            R.id.setting -> Toast.makeText(activity, "rwerqwer", Toast.LENGTH_SHORT).show()
            R.id.btn_logout-> clicklogut()
        }
        layout_drawer.closeDrawers()
        return false
    }

    private fun chattingstart(){
        Log.e("joo", "채팅방으로 이동")
        val intent = Intent(activity, ChatListActivity::class.java)
        startActivity(intent)
    }

    private fun clicklogut(){
        Log.d("123", "로그아웃")
        auth.signOut()
        activity?.let { MySharedPreferences.clearUser(it) }
        startActivity(Intent(activity, LoginActivity::class.java))

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_profile.setOnClickListener {
            startActivity(Intent(activity,UserProfileActivity::class.java))
        }
        menuBar.setOnClickListener {
            layout_drawer.openDrawer(GravityCompat.START)
        }
        naviView.setNavigationItemSelectedListener(this)
    }
}
