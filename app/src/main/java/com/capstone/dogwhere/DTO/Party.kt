package com.capstone.dogwhere.DTO

data class Party(
    val uid: String,
    val dog: String,
    val place: String,
    val place_detail: String,
    val title: String,
    val date: String,
    val startime: String,
    val explanation: String

) {

    constructor() : this("", "","", "", "", "", "", "")
}
