package com.capstone.dogwhere.DTO

data class BBS_Free(
    val uid:String,
    val title: String,
    val content: String,
    val username: String,
    val time :String){
    constructor() : this("","","","","",)
}
