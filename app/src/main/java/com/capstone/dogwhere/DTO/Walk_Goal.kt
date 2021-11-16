package com.capstone.dogwhere.DTO

data class Walk_Goal(

    var uid: String,
    var condition: String,
    var goal_day: Int,
    var minute:Int?=null,
    var km: Int?=null,
    var success_day:Int,
    var percent:Int,
    var condition_date: String?=null
) {
    constructor() : this("","",0,null,null,0,0,)
}