package com.finals.friendsfinder.views.login

import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivityLoginBinding
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.showActivity

class LoginActivity: BaseActivity<ActivityLoginBinding>() {
    override fun observeHandle() {
        super.observeHandle()
    }

    override fun setupView() {
        super.setupView()
    }

    override fun setupEventControl() {
        super.setupEventControl()
        with(rootView){
            btnSignup.clickWithDebounce {
               showActivity<SignUpFragment>()
            }
        }
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }
}