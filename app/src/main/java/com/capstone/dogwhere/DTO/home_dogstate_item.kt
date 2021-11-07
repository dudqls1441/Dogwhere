package com.capstone.dogwhere.DTO

import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.dog_choice_item.view.*
import kotlinx.android.synthetic.main.home_dogstate_item.view.*
import kotlinx.android.synthetic.main.home_hot_bbs_item.view.*
import kotlinx.android.synthetic.main.home_hot_bbs_item.view.bbs_title
import org.jetbrains.anko.db.INTEGER


class home_dogstate_item(
    val dogname: String,
    val docid: String,
    val dogstate: String

) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if(dogstate.equals("false")){
            Glide.with(viewHolder.itemView).load(R.drawable.red_icon_background).circleCrop().into(viewHolder.itemView.img_dogstate)
        }else{
            Glide.with(viewHolder.itemView).load(R.drawable.green_icon_background).circleCrop().into(viewHolder.itemView.img_dogstate)
        }
        viewHolder.itemView.text_dogname.text = dogname

    }

    override fun getLayout(): Int {
        return R.layout.home_dogstate_item
    }
}