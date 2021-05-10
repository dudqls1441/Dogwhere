package com.capstone.dogwhere.DTO

data class Walk_Record(

    val uid: String,
    val time: String,
    val date:String,
    val distance: String
) {
    constructor() : this("","","","")
}