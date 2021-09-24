package com.capstone.dogwhere.DTO

import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.my_ongoing_matching_item.view.*


class Matching_Completed_List_Item(
    val documentId : String,
    val title: String,
    val time: String,
    val place: String
) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_title.text = title
        viewHolder.itemView.text_place.text = place
        viewHolder.itemView.text_time.text = time
    }

    override fun getLayout(): Int {
        return R.layout.my_completed_matching_item
    }
}