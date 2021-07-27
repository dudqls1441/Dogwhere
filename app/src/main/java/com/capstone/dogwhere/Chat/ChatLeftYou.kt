package com.capstone.dogwhere.Chat

import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.chat_left_you.view.*
import kotlinx.android.synthetic.main.chatting_receive_item.view.*
import kotlinx.android.synthetic.main.message_list_row.view.*

class ChatLeftYou(val nickname: String, val msg: String, val time : String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chatting_receive_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_chatroom_nickname.text = nickname
        viewHolder.itemView.text_chatroom_receivemsg.text = msg
        viewHolder.itemView.text_chatroom_receivetime.text = time
    }
}