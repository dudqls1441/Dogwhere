package com.capstone.dogwhere.DTO

import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.matching_list_item.view.*
import kotlinx.android.synthetic.main.my_ongoing_matching_item.view.*
import kotlinx.android.synthetic.main.navi_header.*

class Matching_Reserved_List_Item(
    val leaderUid : String,
    val documentId : String,
    val title: String,
    val time: String,
    val place: String,
    val remaningTime: String
) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_title.text = title
        viewHolder.itemView.text_place.text = place
        viewHolder.itemView.text_time.text = time
        viewHolder.itemView.text_remaining_time.text = remaningTime
    }

    override fun getLayout(): Int {
        return R.layout.my_ongoing_matching_item
    }
}