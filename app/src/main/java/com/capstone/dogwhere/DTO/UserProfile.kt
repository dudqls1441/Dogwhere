package com.capstone.dogwhere.DTO

data class UserProfile(
    val uid: String,
    val profilePhoto: String,
    val UserName: String,
    val UserAge: String,
    val UserSex: String
) {
    constructor() : this("","","","","")

}
