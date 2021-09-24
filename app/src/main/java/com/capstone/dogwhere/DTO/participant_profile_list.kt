package com.capstone.dogwhere.DTO

import android.util.Log
import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.participant_item.view.*

class participant_profile_list(
    var uid: String,
    var username :String,
    var useraddress : String,
    var userimg : String,
    var dogname : String,
    var dogage : String,
    var dogbreed : String,
    var dogimg : String
) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.participant_userimg
        viewHolder.itemView.participant_name.text = username
        viewHolder.itemView.participant_address.text = useraddress
        viewHolder.itemView.participant_dogname.text = dogname
        viewHolder.itemView.participant_breed.text = dogbreed
        viewHolder.itemView.participant_dogage.text = dogage
        viewHolder.itemView.participant_dog_img
        Glide.with(viewHolder.itemView).load(userimg).circleCrop().into(viewHolder.itemView.participant_userimg)
        Glide.with(viewHolder.itemView).load(dogimg).circleCrop().into(viewHolder.itemView.participant_dog_img)
    }

    override fun getLayout(): Int {
        return R.layout.participant_item
    }
}

