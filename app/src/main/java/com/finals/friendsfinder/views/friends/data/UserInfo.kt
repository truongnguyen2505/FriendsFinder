package com.finals.friendsfinder.views.friends.data

data class UserInfo(
    var userId: String = "",
    var userName: String = "",
    var email: String = "",
    var password: String = "",
    val avatar: String = "",
    var shareLocation: String = "",
    var online: String = "",
    val location: String = "",
    var address: String = "",
    val updatedLocation: String = "")
