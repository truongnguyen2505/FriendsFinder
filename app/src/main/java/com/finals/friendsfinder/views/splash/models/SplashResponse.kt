package com.finals.friendsfinder.views.splash.models

import com.google.gson.annotations.SerializedName
import com.finals.friendsfinder.bases.BaseResponse

class SplashResponse : BaseResponse<SplashModel>()

class SplashModel {
    @SerializedName("token")
    var token: String? = null

}