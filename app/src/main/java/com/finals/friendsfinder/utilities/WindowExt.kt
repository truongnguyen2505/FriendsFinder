package com.finals.friendsfinder.utilities

import android.content.Context
import android.view.Window
import android.view.WindowManager

fun Window.changeStatusBarColor(context: Context, color: Int) {
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    statusBarColor = context.resources.getColor(color)
}