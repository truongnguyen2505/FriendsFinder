package com.finals.friendsfinder.views.home

import android.os.Bundle
import android.view.LayoutInflater
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentMenuBinding
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.UserDefaults
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.home.menu.ChangePasswordFragment
import com.finals.friendsfinder.views.home.menu.MyAccountFragment
import com.finals.friendsfinder.views.home.menu.PrivacyPolicyFragment
import com.finals.friendsfinder.views.home.menu.SettingProfile
import com.finals.friendsfinder.views.home.menu.TermsOfUsesFragment
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
                showMessage(title = "Confirm",
                    message = "Are you sure you want to sign out of this device?",
                    txtBtnOk = "Yes",
                    enableCancel = true,
                    listener = object : NotifyDialog.OnDialogListener {
                        override fun onClickButton(isOk: Boolean) {
                            if (isOk) {
                                (activity as? MainActivity)?.setLogout(true)
                                val user = Utils.shared.getUser()
                                user?.online = "0"
                                FirebaseDatabase.getInstance().getReference(TableKey.USERS.key)
                                    .child("${user?.userId}").setValue(user).addOnCompleteListener {
                                        BaseAccessToken.accessToken = ""
                                        UserDefaults.standard.setSharedPreference(
                                            Constants.CURRENT_USER,
                                            ""
                                        )
                                        activity?.showActivity<LoginActivity>(goRoot = true)
                                    }
                            }
                        }
                    }
                )
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
            btnAccount.clickWithDebounce {
                activity?.addFragmentToBackstack(
                    android.R.id.content,
                    MyAccountFragment.newInstance()
                )
            }
            btnMyQr.clickWithDebounce {
                activity?.addFragmentToBackstack(android.R.id.content, MyQRFragment.newInstance())
            }
            btnPolicy.clickWithDebounce {
                activity?.addFragmentToBackstack(
                    android.R.id.content,
                    PrivacyPolicyFragment.newInstance()
                )
            }
            btnTermsOfUses.clickWithDebounce {
                activity?.addFragmentToBackstack(
                    android.R.id.content,
                    TermsOfUsesFragment.newInstance()
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