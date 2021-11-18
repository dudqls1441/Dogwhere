package com.capstone.dogwhere.GroupChat

import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chatting_receive_item.view.*

class ChatLeftYou(val nickname: String, val msg: String, val time : String, val profilephoto: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chatting_receive_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_chatroom_nickname.text = nickname
        viewHolder.itemView.text_chatroom_receivemsg.text = msg
        viewHolder.itemView.text_chatroom_receivetime.text = time
        Glide.with(viewHolder.itemView)
            .load(profilephoto)
            .into(viewHolder.itemView.img_chatroom_receiveprofile)
    }
}