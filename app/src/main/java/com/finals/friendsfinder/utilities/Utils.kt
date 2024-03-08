package com.finals.friendsfinder.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.Surface
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.finals.friendsfinder.R
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.reflect.Type

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (isShow) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        } else {
            inputMethodManager.hideSoftInputFromWindow(
                editText.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
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
        if (activity.currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
            activity.window.decorView
                .windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun showKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return try {
            val androidId = Settings.Secure.getString(
                mContext?.contentResolver,
                Settings.Secure.ANDROID_ID
            )
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

    fun convertToTimeNoDay(
        dateTime: String,
        format: String = "dd/M/yyyy, hh:mm '(GMT +7)'"
    ): String {
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

    fun convertToTimeWithDay(
        dateTime: String,
        format: String = "E, dd/M/yyyy, hh:mm '(GMT +7)'"
    ): String {
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

    fun autoGenerateId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    fun getDateTimeNow(): String{
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
            val netDate = Date(System.currentTimeMillis())
            sdf.format(netDate)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getRawBytes(result: Result): ByteArray? {
        val metadata = result.resultMetadata ?: return null
        val segments = metadata[ResultMetadataType.BYTE_SEGMENTS] ?: return null
        var bytes = ByteArray(0)
        @Suppress("UNCHECKED_CAST")
        for (seg in segments as Iterable<ByteArray>) {
            bytes += seg
        }
        // byte segments can never be shorter than the text.
        // Zxing cuts off content prefixes like "WIFI:"
        return if (bytes.size >= result.text.length) bytes else null
    }

    fun getBitmapFromNestedScrollView(
        context: Context,
        nestedScrollView: NestedScrollView
    ): Bitmap {
        // Measure the total height of the content inside NestedScrollView
        var totalHeight = 0
        for (i in 0 until nestedScrollView.childCount) {
            totalHeight += nestedScrollView.getChildAt(i).height
        }

        // Create a bitmap with the same width as the NestedScrollView and total height of the content
        val returnedBitmap =
            Bitmap.createBitmap(nestedScrollView.width, totalHeight, Bitmap.Config.ARGB_8888)

        // Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(ContextCompat.getColor(context, R.color.white))

        // Iterate through all child views and draw them on the canvas
        for (i in 0 until nestedScrollView.childCount) {
            val childView = nestedScrollView.getChildAt(i)

            // Draw the child view on the canvas
            childView.draw(canvas)

            // Move the canvas down to draw the next child view
            canvas.translate(0f, childView.height.toFloat())
        }

        // Return the bitmap
        return returnedBitmap
    }

    fun saveBitmapImage(context: Context, bitmap: Bitmap, inSuccess: ((uri: Uri?, file: File?)-> Unit)) {
        val timestamp = System.currentTimeMillis()

        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/" + context.getString(R.string.app_name)
            )
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            if (uri != null) {
                try {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.close()
                        } catch (e: Exception) {
                            android.util.Log.e("TAG", "saveBitmapImage: ", e)
                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    context.contentResolver.update(uri, values, null, null)
                    //Log.e("TAG", "saveBitmapImage: sucess 1")
                    inSuccess.invoke(uri, null)
                } catch (e: Exception) {
                    android.util.Log.e("TAG", "saveBitmapImage: ", e)
                }
            }
        } else {
            val imageFileFolder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + '/' + context.getString(R.string.app_name)
            )
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "my_qr_$timestamp.png"
            val imageFile = File(imageFileFolder, mImageName)
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    android.util.Log.e("TAG", "saveBitmapImage: ", e)
                }
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                //Log.e("TAG", "saveBitmapImage: sucess 2")
                inSuccess.invoke(null, imageFile)
            } catch (e: Exception) {
                android.util.Log.e("TAG", "saveBitmapImage: ", e)
            }
        }
    }


}