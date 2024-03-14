package com.finals.friendsfinder.views.chatting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.finals.friendsfinder.bases.BaseViewHolder
import com.finals.friendsfinder.databinding.ItemSearchByNameBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.friends.data.UserDTO
import com.finals.friendsfinder.views.friends.data.UserInfo

class CreateGroupAdapter(
    private val context: Context,
    private val onItemClick: ((UserDTO) -> Unit)
) :
    RecyclerView.Adapter<CreateGroupAdapter.SearchGroupVH>() {

    private val listUser: MutableList<UserDTO> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CreateGroupAdapter.SearchGroupVH {
        val binding = ItemSearchByNameBinding.inflate(LayoutInflater.from(context))
        return SearchGroupVH(binding).apply {
            setupView()
        }
    }

    override fun onBindViewHolder(holder: CreateGroupAdapter.SearchGroupVH, position: Int) {
        holder.bindData(listUser, position)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    fun clearList() {
        listUser.clear()
        notifyDataSetChanged()
    }

    fun setList(newList: List<UserDTO>) {
        listUser.clear()
        listUser.addAll(newList)
        notifyDataSetChanged()
    }

    inner class SearchGroupVH(binding: ItemSearchByNameBinding) :
        BaseViewHolder<ItemSearchByNameBinding, MutableList<UserDTO>>(binding) {
        override fun bindData(data: MutableList<UserDTO>, position: Int) {
            super.bindData(data, position)
            val item = listUser[position]
            with(binding) {
                view.isVisible = position != listUser.size.minus(1)
                tvName.text = item.userName
                tvDesc.text = item.phone
                imgSelect.isSelected = item.isSelected
                layoutMain.clickWithDebounce {
                    item.isSelected = !item.isSelected
                    imgSelect.isSelected = item.isSelected
                    onItemClick.invoke(item)
                }
            }
        }

    }
}