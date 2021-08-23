package com.capstone.dogwhere.DTO

data class Join_User(
                     val userId : String,
                     val pw : String,
                     val pwcheck : String,
                     val userName : String,
                     val userPhone : String,
                     val address : String ){
    constructor() : this("","","","","","")
}