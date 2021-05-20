import com.capstone.dogwhere.BBS_post
import com.capstone.dogwhere.R

//package com.capstone.dogwhere


import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.dogwhere.BBS_Common_Writing
import com.capstone.dogwhere.DTO.BBS_Common
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_common_bbs.*
import kotlinx.android.synthetic.main.imformation_bbs_item.view.*
import kotlin.collections.ArrayList

class MatchingChatFragment : Fragment() {

    private val TAG = MatchingChatFragment::class.java.simpleName
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.matching_chat_fragment, container, false)

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}

