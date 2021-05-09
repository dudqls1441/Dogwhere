package com.capstone.dogwhere.DTO

data class UserProfile(
    val uid: String,
    val profilePhoto: String,
    val UserAge: String,
    val UserName: String,
    val UserSex: String
) {
    constructor() : this("","","","","")

}
