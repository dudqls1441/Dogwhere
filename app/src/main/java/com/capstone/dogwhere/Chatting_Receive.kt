package com.capstone.dogwhere

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chatting_receive_item.view.*


class Chatting_Receive(val message :String, val time :String):Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chatting_receive_item

    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_chatroom_receivemsg.text =message
        viewHolder.itemView.text_chatroom_receivetime.text =time

    }


}