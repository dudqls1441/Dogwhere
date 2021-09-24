package com.capstone.dogwhere.DTO

data class Participant(
    val matchingLeaderUid: String,
    val uid: String,
    val time : String
) {

    constructor() : this("", "","")
}
