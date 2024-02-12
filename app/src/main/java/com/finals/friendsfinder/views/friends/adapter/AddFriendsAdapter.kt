package com.finals.friendsfinder.views.friends.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finals.friendsfinder.bases.BaseViewHolder
import com.finals.friendsfinder.databinding.ItemAddFriendsBinding
import com.finals.friendsfinder.views.friends.data.UserInfo

class AddFriendsAdapter(private val context: Context) :
    RecyclerView.Adapter<AddFriendsAdapter.AddFriendVH>() {

    private val listUser: MutableList<UserInfo> = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddFriendsAdapter.AddFriendVH {
        val binding = ItemAddFriendsBinding.inflate(LayoutInflater.from(context))
        return AddFriendVH(binding).apply {
            setupView()
        }
    }

    override fun onBindViewHolder(holder: AddFriendsAdapter.AddFriendVH, position: Int) {
        holder.bindData(listUser, position)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    inner class AddFriendVH(binding: ItemAddFriendsBinding) :
        BaseViewHolder<ItemAddFriendsBinding, MutableList<UserInfo>>(binding) {
        override fun bindData(data: MutableList<UserInfo>, position: Int) {
            super.bindData(data, position)
            val item = listUser[position]
            with(binding){
                tvName.text = item.userName
            }
        }
    }
}