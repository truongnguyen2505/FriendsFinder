package com.finals.friendsfinder.views.chatting

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentCreateGroupBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.chatting.adapter.CreateGroupAdapter
import com.finals.friendsfinder.views.friends.data.UserDTO
import com.finals.friendsfinder.views.friends.data.UserInfo

class CreateGroupFragment : BaseFragment<FragmentCreateGroupBinding>() {


    companion object {
        private const val TAG = "CreateGroupFragment"
        private const val USER_LIST = "USER_LIST"
        fun newInstance(listUser: ArrayList<UserInfo?>): CreateGroupFragment {
            val arg = Bundle().apply {
                putParcelableArrayList(USER_LIST, listUser)
            }
            return CreateGroupFragment().apply {
                arguments = arg
            }
        }
    }

    private var listUser: MutableList<UserInfo> = mutableListOf()
    private var searchByNameAdapter: CreateGroupAdapter? = null

    override fun observeHandle() {
        super.observeHandle()
        getArg()
    }

    private fun getArg() {
        arguments?.let {
            listUser = it.getParcelableArrayList(USER_LIST) ?: arrayListOf()
        }
    }

    override fun setupView() {
        super.setupView()
        setAdapter()
    }

    override fun bindData() {
        super.bindData()
        with(rootView) {
            edtSearch.apply {
                addTextChangedListener {
                    val txt = it.toString().trim()
                    if (txt.isEmpty()) {
                        searchByNameAdapter?.clearList()
                        return@addTextChangedListener
                    } else {
                        val listSearch = listUser.filter {
                            it.userName.contains(txt)
                        }
                        tvNoData.isVisible = listSearch.isEmpty()
                        rvSearch.isVisible = listSearch.isNotEmpty()
                        val listUserDTO: MutableList<UserDTO> = mutableListOf()
                        listSearch.forEach { info ->
                            val userDTO = UserDTO()
                            userDTO.userName = info.userName
                            userDTO.userId = info.userId
                            userDTO.phone = info.phoneNumber
                            listUserDTO.add(userDTO)
                        }
                        searchByNameAdapter?.setList(listUserDTO)
                    }
                }
            }
            btnBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun setAdapter() {
        searchByNameAdapter = CreateGroupAdapter(requireContext(), onItemClick = {

        })
        rootView.rvSearch.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchByNameAdapter
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentCreateGroupBinding {
        return FragmentCreateGroupBinding.inflate(inflater)
    }
}