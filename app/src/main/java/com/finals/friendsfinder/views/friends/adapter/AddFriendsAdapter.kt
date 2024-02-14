package com.finals.friendsfinder.views.friends.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseViewHolder
import com.finals.friendsfinder.databinding.ItemAddFriendsBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.friends.data.UserInfo
import okhttp3.internal.notify

class AddFriendsAdapter(private val context: Context, private val onClickItem: ((UserInfo) -> Unit)) :
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

    fun setList(newList: List<UserInfo>){
        listUser.clear()
        listUser.addAll(newList)
        notifyDataSetChanged()
    }

    inner class AddFriendVH(binding: ItemAddFriendsBinding) :
        BaseViewHolder<ItemAddFriendsBinding, MutableList<UserInfo>>(binding) {
        override fun bindData(data: MutableList<UserInfo>, position: Int) {
            super.bindData(data, position)
            val item = listUser[position]
            with(binding){
                if (item.imageProfile.isEmpty())
                    imgAvatar.setImageResource(R.drawable.ic_avatar_empty_25)
                else Glide.with(context).load(item.imageProfile).into(imgAvatar)
                tvName.text = item.userName
                layoutItem.clickWithDebounce {
                    onClickItem.invoke(item)
                }
            }
        }
    }
}