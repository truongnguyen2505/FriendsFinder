package com.finals.friendsfinder.applications

import android.app.Application
import com.finals.friendsfinder.utilities.UserDefaults
import com.finals.friendsfinder.utilities.Utils

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Load util class
        Utils.load(this)
        //Load user default
        UserDefaults.standard.load(this)
        //Load saved language
    }

}