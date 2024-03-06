package com.finals.friendsfinder.views.home

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentSearchBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.friends.data.UserInfo

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    companion object {
        private const val TAG = "SearchFragment"
        private const val USER_LIST = "USER_LIST"
        fun newInstance(listUser: ArrayList<UserInfo?>): SearchFragment {
            val arg = Bundle().apply {
                putParcelableArrayList(USER_LIST, listUser)
            }
            return SearchFragment().apply {
                arguments = arg
            }
        }
    }

    private var listUser: MutableList<UserInfo> = mutableListOf()

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
    }

    override fun bindData() {
        super.bindData()
        with(rootView) {
            btnBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
            edtSearch.addTextChangedListener {
                val txt = it.toString().trim()
                if (txt.isEmpty()) {
                    return@addTextChangedListener
                } else {

                }
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater)
    }
}