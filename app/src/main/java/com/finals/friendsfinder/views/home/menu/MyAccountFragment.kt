package com.finals.friendsfinder.views.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentMyAccountBinding
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.UserDefaults
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.finals.friendsfinder.views.friends.data.UserLocationDTO
import com.finals.friendsfinder.views.home.MainActivity
import com.finals.friendsfinder.views.login.LoginActivity
import com.google.firebase.database.FirebaseDatabase

class MyAccountFragment : BaseFragment<FragmentMyAccountBinding>() {

    companion object {
        fun newInstance(userLocationDTO: UserLocationDTO): MyAccountFragment {
            val arg = Bundle().apply {
                putParcelable("USER_INFO", userLocationDTO)
            }
            return MyAccountFragment().apply {
                arguments = arg
            }
        }
    }

    private var userLocationDTO: UserLocationDTO? = null

    override fun observeHandle() {
        super.observeHandle()
    }

    override fun bindData() {
        super.bindData()
        arguments?.let {
            userLocationDTO = it.getParcelable("USER_INFO")
        }
        rootView.tvName.text = userLocationDTO?.userName?:""
        rootView.btnAddFriend.isVisible = userLocationDTO?.friend != "2"
        rootView.imgEdit.isVisible = userLocationDTO?.friend != "2"
        rootView.btnBlock.isVisible = userLocationDTO?.friend == "2"
        rootView.btnFriend.isVisible = userLocationDTO?.friend == "2"
        rootView.btnMap.isVisible = userLocationDTO?.friend == "2"
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
            btnMap.clickWithDebounce {
                activity?.addFragmentToBackstack(android.R.id.content,MapFriendFragment())
            }
            btnBlock.clickWithDebounce {
                showMessage(title = "Confirm",
                    message = "Are you sure you want to block this user?",
                    txtBtnOk = "Yes",
                    enableCancel = true,
                    listener = object : NotifyDialog.OnDialogListener {
                        override fun onClickButton(isOk: Boolean) {
                            if (isOk) {
                            }
                        }
                    }
                )
            }
            btnFriend.clickWithDebounce {
                showMessage(title = "Confirm",
                    message = "Are you sure you want to unfriend this user?",
                    txtBtnOk = "Yes",
                    enableCancel = true,
                    listener = object : NotifyDialog.OnDialogListener {
                        override fun onClickButton(isOk: Boolean) {
                            if (isOk) {
                            }
                        }
                    }
                )
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