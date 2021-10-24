package com.capstone.dogwhere.DTO

import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

import kotlinx.android.synthetic.main.dog_profile_item.view.*


class Dog_Profile_Item (
    val uid:String,
    val docId :String,
        val age: String,
        val name: String,
        val breed: String,
        val sex: String,
        val img : String
    ) : Item<GroupieViewHolder>() {


        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.dog_age.text = age
            viewHolder.itemView.dog_breed.text = breed
            viewHolder.itemView.dog_name.text = name
            viewHolder.itemView.dog_sex.text = sex
            Glide.with(viewHolder.itemView).load(img).circleCrop().into(viewHolder.itemView.img_dog_profile)

        }

        override fun getLayout(): Int {
            return R.layout.dog_profile_item
        }
}