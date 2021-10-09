package com.capstone.dogwhere.DTO

import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.transaction_bbs_item.view.*


class BBS_TransactionItem(val title :String,val content:String, val username:String,val time:String,
                          val price:String, val uid:String, val oid:String, val heartCnt :Int,
                          val visitCnt: Int) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.transaction_bbs_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.imformation_bbs_title.setText(title)
        viewHolder.itemView.imformation_bbs_content.setText(content)
        viewHolder.itemView.imformation_bbs_name.setText(username)
        viewHolder.itemView.imformation_bbs_time.setText(time)
        viewHolder.itemView.imformation_bbs_price.setText(price)
        viewHolder.itemView.imformation_bbs_heartcount.setText(heartCnt.toString())
        viewHolder.itemView.imformation_bbs_visitcount.setText(visitCnt.toString())
    }
}