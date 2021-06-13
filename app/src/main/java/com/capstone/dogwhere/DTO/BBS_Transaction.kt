package com.capstone.dogwhere.DTO

import android.net.Uri
import com.google.android.gms.tasks.Task

data class BBS_Transaction(
    val uid:String,
    val title: String,
    val price:String,
    val content: String,
    val uri: String,
    val username: String,
    val time:String){
    constructor() : this("","","","","","", "")
}