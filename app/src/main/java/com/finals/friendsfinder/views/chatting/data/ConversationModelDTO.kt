package com.finals.friendsfinder.views.chatting.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ConversationModelDTO(
    var conversationId: String? = "",
    var conversationName: String? = "",
    var secondConversationName: String? = "",
    var createAt: String? = "",
    var creatorId: String? = "",
    var typeGroup: String? = ""
) : Parcelable
