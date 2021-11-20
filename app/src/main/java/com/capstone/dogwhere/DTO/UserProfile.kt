package com.capstone.dogwhere.DTO

data class UserProfile(
    val uid: String,
    val profilePhoto: String,
    val userAge: String,
    val userName: String,
    val userSex: String,
    val userToken :String
) {
    constructor() : this("","","","","","")

}
