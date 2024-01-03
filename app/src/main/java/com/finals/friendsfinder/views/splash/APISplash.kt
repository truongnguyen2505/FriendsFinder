package com.finals.friendsfinder.views.splash

import com.finals.friendsfinder.views.splash.models.SplashResponse
import io.reactivex.Observable
import retrofit2.http.GET

interface APISplash {

    @GET("/api/list")
    fun getList(): Observable<SplashResponse>

}