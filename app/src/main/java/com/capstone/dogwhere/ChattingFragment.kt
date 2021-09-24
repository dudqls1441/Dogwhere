package com.capstone.dogwhere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.Chat.ChatNewModel
import com.capstone.dogwhere.Chat.ChatRoomActivity
import com.capstone.dogwhere.Chat.MyChatList
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chatting.*
import java.text.SimpleDateFormat
import java.util.*

class ChattingFragment : Fragment(){
    companion object{
        fun newInstance():ChattingFragment{
            return ChattingFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_chatting, container, false)
        return  view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val myUid = auth.uid
        val adapter = GroupAdapter<GroupieViewHolder>()
        val database = FirebaseDatabase.getInstance()
        val db = Firebase.firestore
        val myRef = database.getReference("message-user-list").child(myUid.toString())


        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("joo", "snapshot: " + snapshot)

                val model = snapshot.getValue(ChatNewModel::class.java)
                val msg = model?.message
                val who = model?.who
                val myuid = model?.myUid
                val youruid = model?.yourUid
                val nickname = model?.nickname
                val time = model?.time
                val t_date = time?.let { Date(it.toLong()) }
                // 날짜, 시간을 가져오고 싶은 형태 선언
                val t_dateFormat = SimpleDateFormat("h:mm a", Locale("ko", "KR"))
                // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
                val str_date = t_dateFormat.format(t_date)
                db.collection("users").document(youruid.toString())
                    .collection("userprofiles").document(youruid.toString())
                    .get()
                    .addOnSuccessListener {
                        val result = it.toObject<UserProfile>()
                        val profilephoto = result?.profilePhoto
                        adapter.add(MyChatList(msg, str_date, nickname, profilephoto, youruid))
                    }
                // 현재 시간을 Date 타입으로 변환




                Log.d("joo", "p0: " + msg)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        recycler_chatlist_list.adapter = adapter
        myRef.addChildEventListener(childEventListener)


        adapter.setOnItemClickListener{ item, view ->

            val uid = (item as MyChatList).youruid
            val name = (item as MyChatList).nickname

            val intent = Intent(context, ChatRoomActivity::class.java)
            Log.e("joo", "yourUid:"+uid+"  name:"+name)
            intent.putExtra("yourUid", uid)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }
}