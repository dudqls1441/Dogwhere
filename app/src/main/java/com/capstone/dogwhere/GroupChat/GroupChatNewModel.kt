package com.capstone.dogwhere.GroupChat

data class GroupChatNewModel(val writerUid : String,
                        val groupchatId : String,
                        val nickname : String,
                        val message : String,
                        val time : Long){
    constructor() : this("","","","",0)
}