package com.capstone.dogwhere.DTO

data class Matching_InUsers(
    val matchingLeaderUid: String,
    val uid: String,
    val title: String,
    val documentId: String

) {

    constructor() : this("", "", "", "")
}
