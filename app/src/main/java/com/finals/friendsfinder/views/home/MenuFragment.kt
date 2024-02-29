package com.finals.friendsfinder.views.home

import android.os.Bundle
import android.view.LayoutInflater
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentMenuBinding
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.home.menu.ChangePasswordFragment
import com.finals.friendsfinder.views.login.LoginActivity

class MenuFragment : BaseFragment<FragmentMenuBinding>() {

    companion object {
        fun newInstance(): MenuFragment {
            val arg = Bundle()
            return MenuFragment().apply {
                arguments = arg
            }
        }
    }

    var onBackEvent: (() -> Unit)? = null
    override fun bindData() {
        super.bindData()
        with(rootView) {
            btnLogout.clickWithDebounce {
                BaseAccessToken.accessToken = ""
                activity?.showActivity<LoginActivity>(goRoot = true)
            }
            btnClose.clickWithDebounce {
                onBackEvent?.invoke()
                activity?.supportFragmentManager?.popBackStack()
            }
            btnChangePass.clickWithDebounce {
                activity?.addFragmentToBackstack(
                    android.R.id.content,
                    ChangePasswordFragment.newInstance()
                )
            }
        }
    }

    override fun setupEventControl() {
        super.setupEventControl()
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentMenuBinding {
        return FragmentMenuBinding.inflate(inflater)
    }
}