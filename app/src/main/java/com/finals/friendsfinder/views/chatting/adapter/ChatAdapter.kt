package com.finals.friendsfinder.views.chatting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finals.friendsfinder.R
import com.finals.friendsfinder.customizes.CircleImageView
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.commons.TypeChat
import com.finals.friendsfinder.views.chatting.data.MessageModel
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatAdapter(private val context: Context, private val listUser: List<UserInfo>, private val myName: String) :
    RecyclerView.Adapter<ChatAdapter.ChatVH>() {

    private val chatList: MutableList<MessageModel> = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ChatAdapter.ChatVH {
        val view = if (viewType == TypeChat.LEFT.key) LayoutInflater.from(context)
            .inflate(R.layout.item_left_chat, parent, false)
        else LayoutInflater.from(context).inflate(R.layout.item_right_chat, parent, false)
        return ChatVH(view)
    }

    override fun getItemViewType(position: Int): Int {
        val currentIdUser = BaseAccessToken.accessToken
        return if (chatList[position].userId == currentIdUser)
            TypeChat.RIGHT.key
        else TypeChat.LEFT.key
    }

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        val item = chatList[position]
        holder.apply {
            tvMess.text = item.message
            val mList = listUser.filter {
                it.userId == item.userId
            }
            val nameUser = if (mList.isEmpty()){
                myName
            }else{
                mList.firstOrNull()?.userName ?: ""
            }
            tvNameUserChat.text = nameUser
            Glide.with(context).load(R.drawable.ic_avatar_empty_25).placeholder(R.drawable.ic_avatar_empty_25)
                .into(imgAvatar)
        }
    }

    fun setList(chatModel: List<MessageModel>){
        chatList.clear()
        chatList.addAll(chatModel)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class ChatVH(v: View) : RecyclerView.ViewHolder(v) {
        val imgAvatar = v.findViewById<CircleImageView>(R.id.imgAvatar)
        val tvMess = v.findViewById<TextView>(R.id.tvMess)
        val tvNameUserChat = v.findViewById<TextView>(R.id.tvNameUserChat)
    }
}