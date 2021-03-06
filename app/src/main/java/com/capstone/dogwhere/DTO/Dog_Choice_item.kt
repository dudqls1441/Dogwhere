package com.capstone.dogwhere.DTO

import android.opengl.Visibility
import android.util.Log
import com.bumptech.glide.Glide
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

import kotlinx.android.synthetic.main.dog_choice_item.view.*
import kotlinx.android.synthetic.main.dog_choice_item.view.dog_age
import kotlinx.android.synthetic.main.dog_choice_item.view.dog_breed
import kotlinx.android.synthetic.main.dog_choice_item.view.dog_name
import kotlinx.android.synthetic.main.dog_choice_item.view.dog_neutralization
import kotlinx.android.synthetic.main.dog_choice_item.view.dog_sex
import kotlinx.android.synthetic.main.dog_choice_item.view.img_dog_profile
import kotlinx.android.synthetic.main.dog_profile_item.view.*


class Dog_Choice_item (
    val uid:String,
    val docId :String,
    val age: String,
    val name: String,
    val breed: String,
    val sex: String,
    val neu:Boolean,
    val img : String,
    var checked:Boolean=false
) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.check.visibility
        viewHolder.itemView.dog_age.text = age
        viewHolder.itemView.dog_breed.text = breed
        viewHolder.itemView.dog_name.text = name
        viewHolder.itemView.dog_sex.text = sex
        if(neu){
            viewHolder.itemView.dog_neutralization.text ="중성화 O"
        } else{
            viewHolder.itemView.dog_neutralization.text = "중성화 X"
        }
        Glide.with(viewHolder.itemView).load(img).centerCrop().circleCrop().into(viewHolder.itemView.img_dog_profile)

        var list=ArrayList<DogProfile>()
        viewHolder.itemView.check.setOnCheckedChangeListener { buttonView, isChecked ->
//            Log.e("yy", "버튼뷰 : "+buttonView.toString())
//            Log.e("yy", "이즈 체크드 : "+ isChecked .toString())
//            Log.e("yy", "포지션 : "+ position .toString())
            if(isChecked){
                checked=isChecked
            }else{

            }

        }
    }

    override fun getLayout(): Int {
        return R.layout.dog_choice_item
    }
}