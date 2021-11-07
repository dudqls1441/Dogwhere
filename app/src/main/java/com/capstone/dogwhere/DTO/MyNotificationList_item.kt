package com.capstone.dogwhere.DTO

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.capstone.dogwhere.R
import kotlinx.android.synthetic.main.notification_list_item.view.*

class MyNotificationList_item(
    val topic: String?,
    val sername: String,
    val message_content: String?,
    val list_time: String?
) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.notification_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.notification_Topic.text = topic
        viewHolder.itemView.notification_username.text = sername
        viewHolder.itemView.notification_message_content.text = message_content
        viewHolder.itemView.notification_list_time.text = list_time
    }
}