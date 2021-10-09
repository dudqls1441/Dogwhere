package com.capstone.dogwhere.DTO

data class BBS_Transaction(
    val uid:String,
    val title: String,
    val price:String,
    val content: String,
    val uri: String,
    val username: String,
    val oid:String,
    val heartCnt: Int,
    val visitCnt: Int,
    val time:String){
    constructor() : this("","","","","","", "", 0, 0, "")
}
