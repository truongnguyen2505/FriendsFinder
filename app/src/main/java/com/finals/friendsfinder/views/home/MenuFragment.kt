package com.finals.friendsfinder.views.home

import android.os.Bundle
import android.view.LayoutInflater
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentMenuBinding
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.UserDefaults
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.home.menu.ChangePasswordFragment
import com.finals.friendsfinder.views.home.menu.SettingProfile
import com.finals.friendsfinder.views.login.LoginActivity
import com.google.firebase.database.FirebaseDatabase

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
                (activity as? MainActivity)?.setLogout(true)
                val user = Utils.shared.getUser()
                user?.online = "0"
                FirebaseDatabase.getInstance().getReference(TableKey.USERS.key)
                    .child("${user?.userId}").setValue(user).addOnCompleteListener {
                        if (it.isSuccessful) {
                            BaseAccessToken.accessToken = ""
                            UserDefaults.standard.setSharedPreference(Constants.CURRENT_USER, "")
                            activity?.showActivity<LoginActivity>(goRoot = true)
                        }
                    }
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
            btnSetting.clickWithDebounce {
                activity?.addFragmentToBackstack(android.R.id.content, SettingProfile.newInstance())
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