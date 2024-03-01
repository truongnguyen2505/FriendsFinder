package com.finals.friendsfinder.views.friends.data

data class UserDTO(
    var userId: String = "",
    var userName: String = "",
    var avatar: String = "",
    var friendId: String = "",
    var receiverId: String = "",
    var friend: String = "",
    var userBlocking: String = "",
    var receiverBlocking: String = "",
    var typeClick: Int = 0
)
