package com.capstone.dogwhere.DTO

import android.net.Uri
import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_b_b_s__common_post.*
import kotlinx.android.synthetic.main.activity_bbs_comment.view.*
import kotlinx.android.synthetic.main.common_bbs_item.view.*

class BBS_CommentItem (
        val uid: String,
        val comment: String,
        val username: String,
        val time: String,
        val profile: String
    ) : Item<GroupieViewHolder>() {


        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.text_bbscomment_comment.text = comment
            viewHolder.itemView.text_bbscomment_name.text = username
            viewHolder.itemView.text_bbscomment_time.text = time
            Glide.with(viewHolder.itemView)
                .load(profile)
                .into(viewHolder.itemView.img_bbscomment_profile)

        }

        override fun getLayout(): Int {
            return R.layout.activity_bbs_comment
        }
}