package com.finals.friendsfinder.views.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentMyAccountBinding
import com.finals.friendsfinder.utilities.clickWithDebounce

class MyAccountFragment : BaseFragment<FragmentMyAccountBinding>() {

    companion object {
        fun newInstance(): MyAccountFragment {
            val arg = Bundle()
            return MyAccountFragment().apply {
                arguments = arg
            }
        }
    }

    override fun observeHandle() {
        super.observeHandle()
    }

    override fun bindData() {
        super.bindData()
        setText()
        setListener()
    }

    override fun setupView() {
        super.setupView()
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
            layoutHeader.tvMessage.text = "My Account"
        }
    }

    override fun setupEventControl() {
        super.setupEventControl()
    }


    override fun getViewBinding(inflater: LayoutInflater): FragmentMyAccountBinding {
        return FragmentMyAccountBinding.inflate(inflater)
    }
}