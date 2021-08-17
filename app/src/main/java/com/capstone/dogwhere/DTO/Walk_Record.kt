package com.capstone.dogwhere.DTO

data class Walk_Record(

    val uid: String,
    val time: String,
    val timesec:String,
    val date:String,
    val distance: String,
    val memo: String
) {
    constructor() : this("","","","","","")
}