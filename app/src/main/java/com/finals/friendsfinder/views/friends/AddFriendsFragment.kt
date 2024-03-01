package com.finals.friendsfinder.views.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.util.Util
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentAddFriendsBinding
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.FriendKey
import com.finals.friendsfinder.utilities.commons.UserKey
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.friends.adapter.AddFriendsAdapter
import com.finals.friendsfinder.views.friends.data.Friends
import com.finals.friendsfinder.views.friends.data.UserDTO
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.finals.friendsfinder.views.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.log

class AddFriendsFragment : BaseFragment<FragmentAddFriendsBinding>() {

    companion object {
        fun newInstance(): AddFriendsFragment {
            val arg = Bundle()
            return AddFriendsFragment().apply {
                arguments = arg
            }
        }
    }

    private var addFriendAdapter: AddFriendsAdapter? = null
    private var currentListUser: MutableList<UserInfo>? = null
    private var currentListFriend: MutableList<Friends>? = null
    override fun observeHandle() {
        super.observeHandle()
        currentListUser = mutableListOf()
        currentListFriend = mutableListOf()
    }

    override fun bindData() {
        super.bindData()
        setText()
        setAdapter()
        getListUser()
        setListener()
    }

    private fun setListener() {
        with(rootView) {
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun setText() {
        with(rootView) {
            layoutHeader.tvMessage.text = "Find Friends"
        }
    }

    private fun getListUser() {
        val firebase = FirebaseAuth.getInstance().currentUser
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference("Users")
        val dbReference2 = db.getReference("Friends")

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentListUser?.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val user = dataSnap.getValue(UserInfo::class.java)
                    //check not me
                    if (!user?.userId.equals(firebase?.uid)) {
                        currentListUser?.add(user!!)
                    }
                }
                //addFriendAdapter?.setList(currentListUser ?: listOf())
            }

            override fun onCancelled(error: DatabaseError) {
                currentListUser?.clear()
            }

        })
        dbReference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentListFriend?.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val friend = dataSnap.getValue(Friends::class.java)
                    currentListFriend?.add(friend!!)
//                    Log.d("TAG", "addValueEventListener: $dataSnap ..... $user")
                }
                //addFriendAdapter?.setListFriend(currentListFriend ?: listOf())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun checkSetList() {
        val currentUser = Utils.shared.getUser()
        val mListUserDTO: MutableList<UserDTO> = mutableListOf()
        if (currentListUser.isNullOrEmpty() && currentListFriend.isNullOrEmpty()){

        }else if (currentListUser.isNullOrEmpty() && !currentListFriend.isNullOrEmpty()){

        }else if (!currentListUser.isNullOrEmpty() && currentListFriend.isNullOrEmpty()){
            currentListUser?.map { user ->
                val userDTO = UserDTO()
                userDTO.friendId = ""
                userDTO.receiverBlocking = ""
                userDTO.userId =  ""
                userDTO.friend = ""
                userDTO.userBlocking = ""
                userDTO.avatar = user.avatar
                userDTO.userName = user.userName
                userDTO.receiverId = ""
                userDTO.typeClick = 0
                mListUserDTO.add(userDTO)
            }
        }else{
            currentListUser?.map { user ->
                currentListFriend?.map { friend ->
                    val userDTO = UserDTO()
                    if (user.userId == friend.userId && currentUser?.userId == friend.receiverId){
                        userDTO.friendId = friend.friendId
                        userDTO.receiverBlocking = friend.receiverBlocking
                        userDTO.userId = friend.userId
                        userDTO.friend = friend.friend
                        userDTO.userBlocking = friend.userBlocking
                        userDTO.avatar = user.avatar
                        userDTO.userName = user.userName
                        userDTO.receiverId = friend.receiverId
                        userDTO.typeClick = 1
                        mListUserDTO.add(userDTO)
                    }else if (user.userId == friend.receiverId && currentUser?.userId == friend.userId){
                        userDTO.friendId = friend.friendId
                        userDTO.receiverBlocking = friend.receiverBlocking
                        userDTO.userId =  friend.userId
                        userDTO.friend = friend.friend
                        userDTO.userBlocking = friend.userBlocking
                        userDTO.avatar = user.avatar
                        userDTO.userName = user.userName
                        userDTO.receiverId = friend.receiverId
                        userDTO.typeClick = 1
                        mListUserDTO.add(userDTO)
                    }else{
                        userDTO.friendId = ""
                        userDTO.receiverBlocking = ""
                        userDTO.userId =  ""
                        userDTO.friend = ""
                        userDTO.userBlocking = ""
                        userDTO.avatar = user.avatar
                        userDTO.userName = user.userName
                        userDTO.receiverId = ""
                        userDTO.typeClick = 0
                        mListUserDTO.add(userDTO)
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        val userLocal = Utils.shared.getUser()
        addFriendAdapter = AddFriendsAdapter(requireContext(), userLocal, onClickItem = {

        })
        addFriendAdapter?.addFriend = { info ->
            updateFriend(info)
        }
        rootView.rvListUser.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = addFriendAdapter
        }

    }

    private fun updateFriend(info: UserInfo) {
        val db = FirebaseDatabase.getInstance()
        val dbReference = db.getReference("Friends")
        val localInfo = Utils.shared.getUser()
        val keyId = Utils.shared.autoGenerateId()
        val hasMap: HashMap<String, String> = HashMap()
        hasMap[FriendKey.FRIEND_ID.key] = keyId
        hasMap[FriendKey.USERID.key] = localInfo?.userId ?: ""
        hasMap[FriendKey.RECEIVER_ID.key] = info.userId ?: ""
        hasMap[FriendKey.IS_FRIEND.key] = "1"
        hasMap[FriendKey.USER_BLOCKING.key] = "0"
        hasMap[FriendKey.RECEIVER_BLOCKING.key] = "0"
        hasMap[FriendKey.CREATE_AT.key] = ""

        dbReference.child(keyId).setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {

            }
        }
    }

    override fun setupEventControl() {
        super.setupEventControl()
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentAddFriendsBinding {
        return FragmentAddFriendsBinding.inflate(inflater)
    }
}