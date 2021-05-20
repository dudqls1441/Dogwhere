package com.capstone.dogwhere

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_list_item.view.*

class MessageListItem(val name :String,val massage:String, val uid:String,val time:String) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_list_username.setText(name)
        viewHolder.itemView.chat_list_time.setText(time)
        viewHolder.itemView.chat_list_message.setText(massage)

    }
}