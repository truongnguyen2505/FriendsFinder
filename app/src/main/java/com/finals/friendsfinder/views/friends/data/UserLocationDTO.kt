package com.finals.friendsfinder.views.friends.data

import com.google.android.gms.maps.model.LatLng

data class UserLocationDTO(
    var userName: String = "",
    var location: LatLng,
    var createAt: String,
    var isOnline: String
)
