package com.finals.friendsfinder.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.provider.Settings
import android.view.Surface
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*


class Utils {

    private val TAG: String = Utils::class.java.simpleName
    private var mContext: Context? = null
    var dpiDevice = 0
    private var screenWidth: Int
    private var screenHeight: Int

    init {
        val displayMetrics = Resources.getSystem().displayMetrics
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
        dpiDevice = displayMetrics.densityDpi
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        var shared: Utils = Utils()

        fun load(context: Context) {
            shared.mContext = context
        }

        fun getDisplayRotation(activity: Activity): Int {
            val rotation = activity.windowManager.defaultDisplay
                .rotation
            when (rotation) {
                Surface.ROTATION_0 -> return 0
                Surface.ROTATION_90 -> return 90
                Surface.ROTATION_180 -> return 180
                Surface.ROTATION_270 -> return 270
            }
            return 0
        }

        fun getDisplayOrientation(degrees: Int, cameraId: Int): Int {
            // See android.hardware.Camera.setDisplayOrientation for
            // documentation.
            val info = CameraInfo()
            Camera.getCameraInfo(cameraId, info)
            var result: Int
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360
                result = (360 - result) % 360 // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360
            }
            return result
        }

    }

    fun showHideKeyBoard(isShow: Boolean, editText: EditText) {
        val inputMethodManager = mContext?.getSystemService(
            Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isShow) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        } else {
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
    }

    fun showHideKeyBoard(activity: Activity, isShow: Boolean) {
        if (isShow) {
            showKeyboard(activity)
        } else {
            hideSoftKeyboard(activity)
        }
    }

    fun checkKeyboardVisible(): Boolean {
        val imm = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.isAcceptingText
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) inputMethodManager.hideSoftInputFromWindow(activity.window.decorView
            .windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun showKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return try {
            val androidId = Settings.Secure.getString(mContext?.contentResolver,
                Settings.Secure.ANDROID_ID)
            md5(androidId).uppercase(Locale.getDefault())
        } catch (e: Exception) {
            Log.e(TAG, "Exception deviceId " + e.message)
            ""
        }

    }

    fun md5(s: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                .getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) {
                var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            Log.d(TAG, e.message)
        }
        return s.uppercase(Locale.getDefault())
    }

    fun convertToTimeNoDay(dateTime: String, format: String = "dd/M/yyyy, hh:mm '(GMT +7)'"): String {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = formatter.parse(dateTime)

            val sdf = SimpleDateFormat(format)
            date?.let {
                return sdf.format(it).uppercase(Locale.getDefault())
            }
            return ""
        } catch (ex: Exception) {
            ""
        }

    }

    fun convertToTimeWithDay(dateTime: String, format: String = "E, dd/M/yyyy, hh:mm '(GMT +7)'"): String {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = formatter.parse(dateTime)

            val sdf = SimpleDateFormat(format)
            date?.let {
                return sdf.format(it).uppercase(Locale.getDefault())
            }
            return ""
        } catch (ex: Exception) {
            ""
        }

    }

    fun getUser(): UserInfo? {
        val userInfo: String =
            (UserDefaults.standard.getSharedPreference(Constants.CURRENT_USER, "")
                ?: "") as String
        val type: Type = object : TypeToken<UserInfo?>() {}.type
        return Gson().fromJson(userInfo, type)
    }



}