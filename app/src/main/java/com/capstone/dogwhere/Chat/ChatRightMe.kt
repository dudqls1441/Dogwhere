package com.capstone.dogwhere.Chat

import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.message_list_row.view.*

class ChatRightMe(): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_right_me
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }
}