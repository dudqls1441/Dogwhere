package com.capstone.dogwhere.DTO

import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_walk__recommend__item.view.*

import kotlinx.android.synthetic.main.dog_profile_item.view.*


class Walk_Recommend_Item (
    val uid:String,
    val documentId:String,
    val title: String,
    val place: String,
    val place_detail: String,
    val date: String,
    val start_time : String
) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.recommend_title.text = title
        viewHolder.itemView.recommend_place.text = place
        viewHolder.itemView.recommend_place_detail.text = place_detail
        viewHolder.itemView.recommend_date.text = date
        viewHolder.itemView.recommend_start_time.text = start_time

    }

    override fun getLayout(): Int {
        return R.layout.activity_walk__recommend__item
    }
}