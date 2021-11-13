package com.capstone.dogwhere.DTO

data class Matching(
    val uid: String,
    val place: String,
    val place_detail: String,
    val title: String,
    val date: String,
    val startime: String,
    val explanation: String,
    val matchingTime : String,
    val doneTime : String,
    val ongoing : Boolean,
    val documentId :String,
    val condition_dog_size: String,
    val condition_dog_neutralization :String,
    val condition_owner_gender : String,
    val latitude : Double,
    val longitude : Double

) {

    constructor() : this("", "","", "", "", "", "","","",true,"","all","all","all", 0.0, 0.0)
}
