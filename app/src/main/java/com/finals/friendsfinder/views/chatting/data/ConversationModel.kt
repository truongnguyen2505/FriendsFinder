package com.finals.friendsfinder.views.chatting.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConversationModel(
    val conversationId: String? = "",
    val conversationName: String? = "",
    val secondConversationName: String? = "",
    val createAt: String? = "",
    val creatorId: String? = "",
    val typeGroup: String? = ""
): Parcelable
