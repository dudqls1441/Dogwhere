package com.capstone.dogwhere.DTO

data class Alarm_data(
    val uid : String,
    val time : String,
    val content : String){
    constructor() : this("","","")
}
