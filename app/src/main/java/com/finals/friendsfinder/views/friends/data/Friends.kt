package com.finals.friendsfinder.views.friends.data

/*
* friend: 0-user, 1-pending, 2-friend
* */
data class Friends(
    val friendId: String = "",
    val userId: String = "",
    val receiverId: String = "",
    var friend: String = "",
    var userBlocking: String = "",
    var receiverBlocking: String = "",
    val createAt: String = ""
)
