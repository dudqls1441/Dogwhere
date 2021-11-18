package com.capstone.dogwhere.GroupChat

import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_right_me.view.*
import kotlinx.android.synthetic.main.chatting_send_item.view.*
import kotlinx.android.synthetic.main.message_list_row.view.*

class ChatRightMe(val msg: String, val time : String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chatting_send_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_chatroom_sendmsg.text = msg
        viewHolder.itemView.text_chatroom_sendtime.text = time
    }
}