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
import com.finals.friendsfinder.views.friends.data.Friends
import com.finals.friendsfinder.views.friends.data.UserDTO
import com.finals.friendsfinder.views.friends.data.UserInfo
import okhttp3.internal.notify

class AddFriendsAdapter(
    private val context: Context,
    private val currentUser: UserInfo?,
    private val onClickItem: ((UserDTO) -> Unit)
) :
    RecyclerView.Adapter<AddFriendsAdapter.AddFriendVH>() {

    //    private val listUser: MutableList<UserInfo> = mutableListOf()
//    private var listFriend: MutableList<Friends> = mutableListOf()
    /*
    * typeClick: 1-user is sender
    * 2-user is receiver
    * else -> 3 */
    private var listUserDTO: MutableList<UserDTO> = mutableListOf()
    var addFriend: ((UserDTO, typeClick: Int) -> Unit)? = null
    var removeFriend: ((UserDTO, typeClick: Int) -> Unit)? = null
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
        holder.bindData(listUserDTO, position)
    }

    override fun getItemCount(): Int {
        return listUserDTO.size
    }

    fun setList(newList: List<UserDTO>) {
        listUserDTO.clear()
        listUserDTO.addAll(newList)
        notifyDataSetChanged()
    }

//    fun setListFriend(mList: List<Friends>) {
//        this.listFriend.clear()
//        this.listFriend.addAll(mList)
//        notifyDataSetChanged()
//    }

    inner class AddFriendVH(binding: ItemAddFriendsBinding) :
        BaseViewHolder<ItemAddFriendsBinding, MutableList<UserDTO>>(binding) {
        override fun bindData(data: MutableList<UserDTO>, position: Int) {
            super.bindData(data, position)
            val item = listUserDTO[position]

            with(binding) {
                if (currentUser?.userId == item.userId && currentUser.userId != item.receiverId) {
                    //case current user is sender
                    checkFriend(position, "Pending")
                } else if (currentUser?.userId == item.receiverId && currentUser.userId != item.userId) {
                    //case current user is receiver
                    checkFriend(position, "Accept")
                } else {
                    btnAdd.text = "Add"
                    btnAdd.clickWithDebounce {
                        addFriend?.invoke(item, 3)
                    }
                }
                if (item.avatar.isEmpty())
                    imgAvatar.setImageResource(R.drawable.ic_avatar_empty_25)
                else Glide.with(context).load(item.avatar).into(imgAvatar)
                tvName.text = item.userName
                layoutItem.clickWithDebounce {
                    onClickItem.invoke(item)
                }
            }

        }

        private fun checkFriend(pos: Int, txtBtn1: String) {
            with(binding) {
                when (listUserDTO[pos].friend) {
                    "0" -> {
                        btnAdd.text = "Add"
                    }

                    "1" -> {
                        btnAdd.text = txtBtn1
                        btnAdd.clickWithDebounce {
                            if (currentUser?.userId == listUserDTO[pos].userId && currentUser.userId != listUserDTO[pos].receiverId) {
                                //case current user is sender
                                removeFriend?.invoke(listUserDTO[pos], 1)
                            } else if (currentUser?.userId == listUserDTO[pos].receiverId && currentUser.userId != listUserDTO[pos].userId) {
                                //case current user is receiver
                                addFriend?.invoke(listUserDTO[pos], 2)
                            } else {
                                addFriend?.invoke(listUserDTO[pos], 3)
                            }
                        }
                    }

                    "2" -> {
                        btnAdd.text = "Friend"
                        btnAdd.clickWithDebounce {
                            if (currentUser?.userId == listUserDTO[pos].userId && currentUser.userId != listUserDTO[pos].receiverId) {
                                //case current user is sender
                                removeFriend?.invoke(listUserDTO[pos], 1)
                            } else if (currentUser?.userId == listUserDTO[pos].receiverId && currentUser.userId != listUserDTO[pos].userId) {
                                //case current user is receiver
                                removeFriend?.invoke(listUserDTO[pos], 1)
                            } else {
                                addFriend?.invoke(listUserDTO[pos], 3)
                            }
                        }
                    }

                    else -> {
                        btnAdd.text = "Add"
                    }
                }
            }
        }
    }
}