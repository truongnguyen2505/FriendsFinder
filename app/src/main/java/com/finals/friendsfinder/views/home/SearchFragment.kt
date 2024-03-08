package com.finals.friendsfinder.views.home

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentSearchBinding
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.finals.friendsfinder.views.home.adapter.SearchByPhoneAdapter

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
    private var searchByPhoneAdapter: SearchByPhoneAdapter? = null

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

    private fun setAdapter() {
        searchByPhoneAdapter = SearchByPhoneAdapter(requireContext())
        rootView.rvSearch.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchByPhoneAdapter
        }
    }

    override fun bindData() {
        super.bindData()
        with(rootView) {
            edtSearch.apply {
                inputType = InputType.TYPE_CLASS_PHONE
                addTextChangedListener {
                    val txt = it.toString().trim()
                    if (txt.isEmpty()) {
                        searchByPhoneAdapter?.clearList()
                        return@addTextChangedListener
                    } else {
                        val listSearch = listUser.filter {
                            it.phoneNumber.contains(txt)
                        }
                        tvNoData.isVisible = listSearch.isEmpty()
                        rvSearch.isVisible = listSearch.isNotEmpty()
                        searchByPhoneAdapter?.setList(listSearch)
                    }
                }
            }
            btnBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
            btnScan.clickWithDebounce {
                activity?.addFragmentToBackstack(
                    android.R.id.content,
                    QRCodeFragment.newInstance(ArrayList(listUser))
                )
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater)
    }
}