package com.finals.friendsfinder.views.splash

import androidx.lifecycle.MutableLiveData
import com.finals.friendsfinder.BuildConfig
import com.finals.friendsfinder.bases.BaseViewModel
import com.finals.friendsfinder.networks.ServicesManager
import com.finals.friendsfinder.utilities.Log
import com.finals.friendsfinder.views.splash.models.SplashResponse

class SplashVM : BaseViewModel() {
    private val apiSplash: APISplash = ServicesManager.builder(BuildConfig.BASE_URL_API)

    var splashResponse: MutableLiveData<SplashResponse>? = null
    var errorResponse: MutableLiveData<Throwable>? = null

    init {
        splashResponse = MutableLiveData()
        errorResponse = MutableLiveData()
    }

    fun getList() {
        request(apiSplash.getList(), onSuccess = {
            Log.d("SplashResponse: ${it.data?.token}")
            splashResponse?.value = it
        }, onFailed = {
            Log.d("Error request: ${it.localizedMessage}")
            errorResponse?.value = it
        })
    }

}