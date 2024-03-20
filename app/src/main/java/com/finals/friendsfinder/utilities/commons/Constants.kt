package com.finals.friendsfinder.utilities.commons

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.finals.friendsfinder.R
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.commons.Constants.Companion.isLockShowPopup

class Constants {
    companion object {
        const val CURRENT_LANGUAGE: String = "CURRENT_LANGUAGE"
        const val EVENT_CHANGE_LANGUAGE: String = "event_change_language"
        const val CURRENT_USER: String = "CURRENT_USER"
        const val TYPE_GROUP: String = "type_group"

        var isLockShowPopup: Boolean = false

        const val DEFAULT_FONT: String = "fonts/Roboto-Regular.ttf"
        const val PAGE_LIMIT: Int = 20
        val LOCATION_PER = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val CONTACT_PER = arrayOf(
            Manifest.permission.READ_CONTACTS
        )

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

enum class UserKey(val key: String) {
    USERID("userId"),
    USERNAME("userName"),
    EMAIL("email"),
    PASSWORD("password"),
    AVATAR("avatar"),
    LOCATION("location"),
    IS_SHARE_LOCATION("shareLocation"),
    IS_ONLINE("online"),
    UPDATED_LOCATION("updatedLocation"),
    ADDRESS("address"),
    PHONE_NUMBER("phoneNumber")
}

enum class FriendKey(val key: String) {
    FRIEND_ID("friendId"),
    USERID("userId"),
    RECEIVER_ID("receiverId"),
    IS_FRIEND("friend"),
    USER_BLOCKING("userBlocking"),
    RECEIVER_BLOCKING("receiverBlocking"),
    CREATE_AT("createAt"),
}

enum class MessageKey(val key: String) {
    MESSAGE_ID("messageId"),
    CREATE_AT("createAt"),
    UPDATE_AT("update_at"),
    MESSAGE("message"),
    USER_ID("userId"),
    CONVERSATION_ID("conversationId")
}

enum class ConversationKey(val key: String) {
    CONVERSATION_ID("conversationId"),
    CONVERSATION_NAME_FOR_RECEIVER("secondConversationName"),
    CREATE_AT("createAt"),
    CREATOR_ID("creatorId"),
    TYPE_GROUP("type_group"),
    CONVERSATION_NAME_FOR_CREATOR("conversationName"),

}

enum class ParticipantKey(val key: String) {
    PARTICIPANT_ID("participantId"),
    USERID("userId"),
    CONVERSATION_ID("conversationId")
}

enum class LocationKey(val key: String) {
    LOCATION_ID("locationId"),
    COORDINATE("coordinate"),
    CREATE_AT("createAt"),
    USER_ID("userId"),
    USERNAME("userName")
}

enum class TableKey(val key: String) {
    USERS("Users"),
    LOCATIONS("Locations"),
    MESSAGES("Messages"),
    FRIENDS("Friends"),
    PARTICIPANTS("Participants"),
    CONVERSATION("Conversations")
}


enum class TypeChat(val key: Int) {
    LEFT(0),
    RIGHT(1),
}