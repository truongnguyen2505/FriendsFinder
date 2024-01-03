package com.finals.friendsfinder.models

import com.finals.friendsfinder.utilities.UserDefaults

class BaseAccessToken {

    companion object {
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val DEVICE_TOKEN = "DEVICE_TOKEN"

        var accessToken: String
            get() {
                return (UserDefaults.standard.getSharedPreference(ACCESS_TOKEN, "") as? String)
                    ?: ""
            }
            set(value) {
                UserDefaults.standard.setSharedPreference(ACCESS_TOKEN, value)
            }

        var refreshToken: String
            get() {
                return (UserDefaults.standard.getSharedPreference(DEVICE_TOKEN, "") as? String)
                    ?: ""
            }
            set(value) {
                UserDefaults.standard.setSharedPreference(DEVICE_TOKEN, value)
            }

        val token: String
            get() {
                return when {
                    accessToken.isNotEmpty() -> {
                        accessToken
                    }
                    refreshToken.isNotEmpty() -> {
                        refreshToken
                    }
                    else -> {
                        ""
                    }
                }
            }
        val isLogin: Boolean
            get() {
                return accessToken.isNotEmpty()
            }
    }
}
