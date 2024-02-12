package com.finals.friendsfinder.views.friends

import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentAddFriendsBinding
import com.finals.friendsfinder.views.friends.adapter.AddFriendsAdapter
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddFriendsFragment : BaseFragment<FragmentAddFriendsBinding>() {

    private var addFriendAdapter: AddFriendsAdapter? = null
    private var currentListUser: MutableList<UserInfo>? = null
    override fun observeHandle() {
        super.observeHandle()
        currentListUser = mutableListOf()
    }

    override fun bindData() {
        super.bindData()
        setAdapter()
    }

    private fun getListUser(){
        val firebase = FirebaseAuth.getInstance().currentUser
        val dbReference = FirebaseDatabase.getInstance().getReference("Users")

        dbReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentListUser?.clear()
                for (dataSnap: DataSnapshot in snapshot.children){
                    val user = dataSnap.getValue(UserInfo::class.java)
                    //check not me
                    if (!user?.userId.equals(firebase?.uid)){
                        currentListUser?.add(user!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                currentListUser?.clear()
            }

        })
    }
    private fun setAdapter() {
        addFriendAdapter = AddFriendsAdapter(requireContext())
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