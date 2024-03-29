package com.finals.friendsfinder.utilities.commons

import android.content.Context
import android.content.res.Configuration
import com.finals.friendsfinder.R
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.commons.Constants.Companion.isLockShowPopup

class Constants {
    companion object {
        const val CURRENT_LANGUAGE: String = "CURRENT_LANGUAGE"
        const val EVENT_CHANGE_LANGUAGE: String = "event_change_language"

        var isLockShowPopup: Boolean = false

        const val DEFAULT_FONT: String = "fonts/Roboto-Regular.ttf"
        const val PAGE_LIMIT: Int = 20

        fun isTablet(context: Context): Boolean {
            return context.resources
                .configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        fun isTabletXML(context: Context): Boolean {
            return context.resources.getBoolean(R.bool.isTablet)
        }

    }
}

enum class MessageStatus(val status: Int) {
    SUCCESS(1),
    WARNING(2),
    INFO(3),
    ERROR(4),
}

fun logout() {
    isLockShowPopup = false
//    UserInfo.clearCache()
    BaseAccessToken.accessToken = ""
    BaseAccessToken.refreshToken = ""
}