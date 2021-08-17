package com.capstone.dogwhere.DTO

class BBS_Comment (
    val uid:String,
    val comment: String,
    val username: String,
    val time:String,
    val userprofile:String){
        constructor() : this("","","","","")
}