package com.capstone.dogwhere.GroupChat

import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_list_item.view.*

class MyChatList(
    val msg: String?,
    val str_date: String,
    val nickname: String?,
    val profilephoto: String?,
    val youruid: String?
) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_list_message.text = msg
        viewHolder.itemView.chat_list_time.text = str_date
        viewHolder.itemView.chat_list_username.text = nickname
        if (profilephoto != null){
            Glide.with(viewHolder.itemView)
                .load(profilephoto)
                .into(viewHolder.itemView.chat_list_userimg)
        }
    }
}
