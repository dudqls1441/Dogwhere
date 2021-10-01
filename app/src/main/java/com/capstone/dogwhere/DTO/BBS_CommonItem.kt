package com.capstone.dogwhere.DTO

import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.common_bbs_item.view.*

class BBS_CommonItem (val title :String,val content:String, val username:String,val time:String,
                      val uid:String, val oid:String, val heartCnt: Int,
                      val visitCnt: Int) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.common_bbs_item
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.free_bbs_title.setText(title)
            viewHolder.itemView.free_bbs_content.setText(content)
            viewHolder.itemView.free_bbs_name.setText(username)
            viewHolder.itemView.free_bbs_time.setText(time)
            viewHolder.itemView.free_bbs_heartcount.setText(heartCnt.toString())
            viewHolder.itemView.free_bbs_visitcount.setText(visitCnt.toString())
        }
}