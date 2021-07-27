package com.capstone.dogwhere

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chatting_send_item.view.*


class Chatting_Send(val message :String, val time :String):Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chatting_send_item

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_chatroom_sendmsg.text =message
        viewHolder.itemView.text_chatroom_sendtime.text =time

    }


}