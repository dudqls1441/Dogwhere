package com.capstone.dogwhere.DTO

import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.home_hot_bbs_item.view.*
import org.jetbrains.anko.db.INTEGER


class home_hot_bbs_Item(
    val title: String,
    val content: String,
    val heart_cnt: Int,
    val view_cnt: Int

) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.bbs_title.text = title
        viewHolder.itemView.bbs_content.text = content
        viewHolder.itemView.heart_count.text = Integer.parseInt(heart_cnt.toString()).toString()
        viewHolder.itemView.view_count.text = Integer.parseInt(view_cnt.toString()).toString()
    }

    override fun getLayout(): Int {
        return R.layout.home_hot_bbs_item
    }
}