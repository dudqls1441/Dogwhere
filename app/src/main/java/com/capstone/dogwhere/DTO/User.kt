package com.capstone.dogwhere.DTO

data class User(val uid : String,
                val userId : String,
                val userName : String,
                val userPhone : String,
                val token :String,
                val registeredProfile : Boolean ){
    constructor() : this("","","","","",false)
}