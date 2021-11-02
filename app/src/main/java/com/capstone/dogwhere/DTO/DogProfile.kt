package com.capstone.dogwhere.DTO

data class DogProfile(    val uid: String,
                          val dogName: String,
                          val photoUrl: String,
                          val dogAge: String,
                          val dogBreed: String,
                          val dogSex: String,
                          val dogSize: String,
                          val neutering: Boolean=false,
                          val dogState : Boolean=true
) {
    constructor() : this("", "", "", "", "","","")
}
