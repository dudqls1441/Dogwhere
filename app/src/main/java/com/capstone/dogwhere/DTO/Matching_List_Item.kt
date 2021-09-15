package com.capstone.dogwhere.DTO

import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.matching_list_item.view.*
import kotlinx.android.synthetic.main.navi_header.*

class Matching_List_Item(
    val uid: String,
    val title: String,
    val time: String,
    val place: String,
    val img: String,
    val documentId : String
) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.title_matching_list.text = title
        viewHolder.itemView.time_matching_list.text = time
        viewHolder.itemView.place_matching_list.text = place
        Glide.with(viewHolder.itemView)
            .load(img).circleCrop()
            .into(viewHolder.itemView.img_matching_list)

    }

    override fun getLayout(): Int {
        return R.layout.matching_list_item
    }
}