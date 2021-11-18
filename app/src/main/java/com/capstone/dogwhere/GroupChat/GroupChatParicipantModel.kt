package com.capstone.dogwhere.GroupChat

data class GroupChatParicipantModel(
    val writerUid: String,
    val documentId: String,
    val nickname: String,
    val matchingTitle: String
) {
    constructor() : this("", "", "", "")
}