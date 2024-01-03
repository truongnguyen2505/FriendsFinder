package com.finals.friendsfinder.utilities

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.Patterns
import com.finals.friendsfinder.BuildConfig

fun String.openLink(context: Context) {
    var url = this
    if (!url.startsWith("http://") && !url.startsWith("https://"))
        url = "http://$url"
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url));
    context.startActivity(browserIntent);
}

fun String?.isValidEmail(): Boolean {
    return !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String?.isValidPhone(): Boolean {
    return !isNullOrEmpty() && Patterns.PHONE.matcher(this).matches()
}

/**
 * Return value of string array on android xml file
 */
fun String.getStringValue(): String {
    return try {
        val resource = Resources.getSystem()
        val resId: Int = resource.getIdentifier(this, "string", BuildConfig.APPLICATION_ID)
        resource.getString(resId)
    } catch (ex: Exception) {
        ""
    }
}