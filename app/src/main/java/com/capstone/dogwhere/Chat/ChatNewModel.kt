package com.capstone.dogwhere.Chat

class ChatNewModel(val myUid : String,
                   val yourUid : String,
                   val nickname : String,
                   val message : String,
                   val time : Long,
                   val who : String){
    constructor() : this("","","","",0,"")
}