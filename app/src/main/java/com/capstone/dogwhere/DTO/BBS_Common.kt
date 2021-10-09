package com.capstone.dogwhere.DTO

data class BBS_Common(
    val uid:String,
    val title: String,
    val content: String,
    val uri: String,
    val username: String,
    val time:String,
    val oid:String,
    val heartCnt: Int,
    val visitCnt: Int){
    constructor() : this("","","","","","", "", 0, 0)
}
