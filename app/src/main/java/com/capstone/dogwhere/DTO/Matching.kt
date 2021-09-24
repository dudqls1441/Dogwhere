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
    val documentId :String

) {

    constructor() : this("", "","", "", "", "", "", "",true,"")
}
