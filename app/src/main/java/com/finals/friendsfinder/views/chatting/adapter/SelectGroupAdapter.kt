package com.finals.friendsfinder.views.chatting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finals.friendsfinder.bases.BaseViewHolder
import com.finals.friendsfinder.databinding.ItemSelectGroupBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.friends.data.UserInfo

class SelectGroupAdapter(private val context: Context) :
    RecyclerView.Adapter<SelectGroupAdapter.SelectGroupVH>() {

    private val listUser: MutableList<UserInfo> = ArrayList()
    var onDeleteItem: ((UserInfo, Int) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setList(us: List<UserInfo>) {
        listUser.clear()
        listUser.addAll(us)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(us: UserInfo) {
        listUser.add(us)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(us: UserInfo) {
        val pos = listUser.indexOf(us)
        listUser.remove(us)
        notifyItemRemoved(pos)
    }
    fun getListSelected(): List<UserInfo>{
        return listUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectGroupVH {
        val binding = ItemSelectGroupBinding.inflate(LayoutInflater.from(context))
        val itemView = SelectGroupVH(binding)
        return itemView
    }

    override fun onBindViewHolder(holder: SelectGroupVH, position: Int) {
        holder.bindData(listUser, position)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    inner class SelectGroupVH(binding: ItemSelectGroupBinding) :
        BaseViewHolder<ItemSelectGroupBinding, MutableList<UserInfo>>(binding) {
        override fun bindData(data: MutableList<UserInfo>, position: Int) {
            super.bindData(data, position)
            val item = listUser[position]

            item.let {
                binding.tvNameCategory.text = it.userName
            }
            binding.imgCancel.clickWithDebounce {
                onDeleteItem?.invoke(item, layoutPosition)
                listUser.remove(item)
                notifyItemRemoved(layoutPosition)
            }
        }
    }
}