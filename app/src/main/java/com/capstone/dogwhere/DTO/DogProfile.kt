package com.capstone.dogwhere.DTO

data class DogProfile(    val uid: String,
                          val dogName: String,
                          val photoUrl: String,
                          val dogAge: String,
                          val dogBreed: String,
                          val dogSex: String
) {

    constructor() : this("", "", "", "", "", "")
}
