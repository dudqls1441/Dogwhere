package com.capstone.dogwhere.DTO

data class DogProfile(
    val uid:String,
    val DogName: String,
    val photoUrl: String,
    val DogAge: String,
    val DogBreed:String, val DogSex:String){
    constructor() : this("","","","","","")
}
