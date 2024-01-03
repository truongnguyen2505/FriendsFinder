package com.finals.friendsfinder.utilities

import android.content.Context
import android.net.ConnectivityManager

fun Context.isConnected(): Boolean {
    return try {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isAvailable
                && connectivityManager.activeNetworkInfo!!.isConnected)
    } catch (ex: Exception) {
        false
    }
}