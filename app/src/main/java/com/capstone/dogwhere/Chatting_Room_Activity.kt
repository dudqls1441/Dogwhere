package com.capstone.dogwhere

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chatting_room.*

class Chatting_Room_Activity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_room)

        auth = FirebaseAuth.getInstance()

        val myuid = auth.uid.toString()
        val name = ""
        val senderid=""

        val adapter=GroupAdapter<GroupieViewHolder>()




    }


    fun hideKeyboard() {
        val hide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(chatting_room_btn_send.windowToken, 0)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainMenuActivity::class.java))
        finish()
    }
}