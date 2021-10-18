package com.capstone.dogwhere.DTO

data class Matching(
    val uid: String,
    val dog: String,
    val place: String,
    val place_detail: String,
    val title: String,
    val date: String,
    val startime: String,
    val explanation: String,
    val isOngoing : Boolean,
    val documentId :String,
    val condition_dog_size: String,
    val condition_dog_neutralization :String,
    val condition_owner_gender : String

) {

    constructor() : this("", "","", "", "", "", "", "",true,"","all","all","all")
}
