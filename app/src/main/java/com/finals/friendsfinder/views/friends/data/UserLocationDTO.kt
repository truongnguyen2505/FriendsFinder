package com.finals.friendsfinder.views.friends.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLocationDTO(
    var userId: String = "",
    var userName: String = "",
    var location: LatLng,
    var createAt: String,
    var isOnline: String,
    var friend :String="",
): Parcelable
