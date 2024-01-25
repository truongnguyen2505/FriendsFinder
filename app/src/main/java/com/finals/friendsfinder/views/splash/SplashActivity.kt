package com.finals.friendsfinder.views.splash

import androidx.lifecycle.ViewModelProvider
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivitySplashBinding
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.home.MainActivity
import com.finals.friendsfinder.views.login.LoginActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    var splashVM: SplashVM? = null

    override fun setupView() {
        super.setupView()
        showActivity<MainActivity>(goRoot = true)
    }

    override fun observeHandle() {
        super.observeHandle()
        //splashVM = ViewModelProvider(this).get(SplashVM::class.java)
//        splashVM?.splashResponse?.observe(this) {
//
//        }
//
//        splashVM?.errorResponse?.observe(this) {
//
//        }
        //splashVM?.getList()
    }

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onLanguageChanged() {
        super.onLanguageChanged()
    }
}