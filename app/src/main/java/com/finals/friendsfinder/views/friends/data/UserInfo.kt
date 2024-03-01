package com.finals.friendsfinder.views.friends.data

data class UserInfo(
    var userId: String = "",
    var userName: String = "",
    var email: String = "",
    var password: String = "",
    val avatar: String = "",
    val shareLocation: String = "",
    var online: String = "",
    val location: String = "",
    val updatedLocation: String = "")
