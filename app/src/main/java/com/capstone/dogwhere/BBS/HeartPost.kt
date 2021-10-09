package com.capstone.dogwhere.BBS

data class HeartPost(
    val postId: String,
    var heart: Boolean
) {
    constructor() : this("", true)

}