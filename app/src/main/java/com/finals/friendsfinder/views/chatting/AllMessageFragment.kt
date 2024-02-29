package com.finals.friendsfinder.views.chatting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentAddFriendsBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.friends.adapter.AddFriendsAdapter
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllMessageFragment : BaseFragment<FragmentAddFriendsBinding>() {


    companion object {
        fun newInstance(): AllMessageFragment {
            val arg = Bundle()
            return AllMessageFragment().apply {
                arguments = arg
            }
        }
    }

    private var addFriendAdapter: AddFriendsAdapter? = null
    private var currentListUser: MutableList<UserInfo>? = null
    override fun observeHandle() {
        super.observeHandle()
        currentListUser = mutableListOf()
    }

    override fun bindData() {
        super.bindData()
        setAdapter()
        getListUser()
        setListener()
    }

    private fun setListener() {
        with(rootView){
            layoutHeader.tvMessage.text = "Messengers"
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun getListUser() {
        val firebase = FirebaseAuth.getInstance().currentUser
        val dbReference = FirebaseDatabase.getInstance().getReference("Users")

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
                addFriendAdapter?.setList(currentListUser ?: listOf())
            }

            override fun onCancelled(error: DatabaseError) {
                currentListUser?.clear()
            }

        })
    }

    private fun setAdapter() {
        addFriendAdapter = AddFriendsAdapter(requireContext(), onClickItem = { userInfo ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
//            intent.putExtra(SignupKey.USERID.key, userInfo.userId)
//            intent.putExtra(SignupKey.USERNAME.key, userInfo.userName)
            startActivity(intent)
        })
        rootView.rvListUser.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = addFriendAdapter
        }

    }

    override fun setupEventControl() {
        super.setupEventControl()
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentAddFriendsBinding {
        return FragmentAddFriendsBinding.inflate(inflater)
    }
}