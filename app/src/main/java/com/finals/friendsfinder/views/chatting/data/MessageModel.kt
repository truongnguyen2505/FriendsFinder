package com.finals.friendsfinder.views.chatting.data

data class MessageModel(
    var messageId: String = "",
    var createAt: String = "",
    var userId: String = "",
    var message: String = "",
    var conversationId: String = ""
)
