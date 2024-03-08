package com.finals.friendsfinder.views.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseViewHolder
import com.finals.friendsfinder.databinding.ItemSearchByPhoneBinding
import com.finals.friendsfinder.views.friends.data.UserInfo

class SearchByPhoneAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<SearchByPhoneAdapter.SearchVH>() {

    private var listUser: MutableList<UserInfo> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchByPhoneAdapter.SearchVH {
        val binding = ItemSearchByPhoneBinding.inflate(LayoutInflater.from(context))
        return SearchVH(binding).apply {
            setupView()
        }
    }

    override fun onBindViewHolder(holder: SearchByPhoneAdapter.SearchVH, position: Int) {
        holder.bindData(listUser, position)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    fun setList(newList: List<UserInfo>) {
        listUser.clear()
        listUser.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearList() {
        listUser.clear()
        notifyDataSetChanged()
    }

    inner class SearchVH(binding: ItemSearchByPhoneBinding) :
        BaseViewHolder<ItemSearchByPhoneBinding, MutableList<UserInfo>>(binding) {
        override fun bindData(data: MutableList<UserInfo>, position: Int) {
            super.bindData(data, position)
            val item = listUser[position]
            with(binding) {
                view.isVisible = position != listUser.size.minus(1)
                tvName.text = item.userName ?: ""
                tvDesc.text = if (item.online == "0")
                    "Offline" else "Online"
                Glide.with(context).load(item.avatar).error(R.drawable.ic_avatar_empty_25)
                    .placeholder(R.drawable.ic_avatar_empty_25).into(imgAvatar)
            }
        }

        private fun checkFriend(pos: Int, txtBtn1: String) {
            with(binding) {

            }
        }
    }
}