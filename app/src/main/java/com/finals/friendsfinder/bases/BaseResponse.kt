package com.finals.friendsfinder.bases

import android.content.res.Resources
import com.google.gson.annotations.SerializedName
import com.finals.friendsfinder.R
import com.finals.friendsfinder.utilities.getStringValue

open class BaseResponse<D> {

    private val REQUEST_SUCCESS = 1

    @SerializedName("signal")
    var signal: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("errorCode")
    var errorCode: String? = null

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("data")
    var data: D? = null

    fun getErrorMessage(): String {
        val resources = Resources.getSystem()
        val defaultError = resources.getString(R.string.ERR_0000_description)
        val message = "${errorCode}_description".getStringValue()
        return if (message.isNotEmpty())
            message
        else
            defaultError
    }

    fun getErrorTitle(): String {
        return errorCode?.getStringValue() ?: ""
    }

    fun isRequestSuccess(): Boolean {
        return signal == REQUEST_SUCCESS
    }

    fun isRequestLogin(): Boolean {
        val errCode = errorCode?.trim()
        if (errCode.isNullOrEmpty()) {
            return false
        }
        return listExpireLogin().contains(errCode)
    }

    fun isNotExistAccount(): Boolean {
        val errCode = errorCode?.trim()
        if (errCode.isNullOrEmpty()) {
            return false
        }
        return listNotExistAccount().contains(errCode)
    }

    private fun listNotExistAccount(): Array<String> {
        return arrayOf()
    }

    fun isExistAccount(): Boolean {
        val errCode = errorCode?.trim()
        if (errCode.isNullOrEmpty()) {
            return false
        }
        return listExistAccount().contains(errCode)
    }

    private fun listExistAccount(): Array<String> {
        return arrayOf()
    }

    private fun listExpireLogin(): Array<String> {
        return arrayOf()
    }
}

class BaseModel : BaseResponse<Any>()