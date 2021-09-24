package com.capstone.dogwhere.DTO

data class participantDTO(
    val uid: String,
    val username :String,
    val useraddress : String,
    val userimg : String,
    val dogname : String,
    val dogage : String,
    val dogbreed : String,
    val dogimg : String
) {

    constructor() : this("","","","","","","","")
}
