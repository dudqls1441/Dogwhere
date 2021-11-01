package com.capstone.dogwhere.DTO

class Matching_MapList_Item (
    val uid: String,
    val place: String,
    val title: String,
    val date: String,
    val startime: String,
    val documentId :String,
    val latitude : Double,
    val longitude : Double
) {

    constructor() : this("", "", "", "", "", "", 0.0, 0.0)
}
