package com.finals.friendsfinder.utilities

import android.util.Log
import com.finals.friendsfinder.BuildConfig

/**
 * Created by TAKASHI20 at 3:23 PM on 03-Dec-16.
 */
object Log {
    private val DEBUG: Boolean = BuildConfig.DEBUG
    private const val TAG = "AI-CHECKIN-LOG"
    fun v(TAG: String?, message: String?) {
        if (DEBUG) Log.v(TAG, message!!)
    }

    fun d(TAG: String?, message: String?) {
        if (DEBUG) Log.d(TAG, message!!)
    }

    fun i(TAG: String?, message: String?) {
        if (DEBUG) Log.i(TAG, message!!)
    }

    fun w(TAG: String?, message: String?) {
        if (DEBUG) Log.w(TAG, message!!)
    }

    fun e(TAG: String?, message: String?) {
        if (DEBUG) Log.e(TAG, message!!)
    }

    fun wtf(TAG: String?, message: String?) {
        if (DEBUG) Log.wtf(TAG, message)
    }

    fun v(message: String?) {
        if (DEBUG) Log.v(
            TAG,
            message!!
        )
    }

    fun d(message: String?) {
        if (DEBUG) Log.d(
            TAG,
            message!!
        )
    }

    fun i(message: String?) {
        if (DEBUG) Log.i(
            TAG,
            message!!
        )
    }

    fun w(message: String?) {
        if (DEBUG) Log.w(
            TAG,
            message!!
        )
    }

    fun e(message: String?) {
        if (DEBUG) Log.e(
            TAG,
            message!!
        )
    }

    fun wtf(message: String?) {
        if (DEBUG) Log.wtf(TAG, message)
    }

    fun v(TAG: String?, message: String, e: Exception) {
        if (DEBUG) Log.v(TAG, message + e.message)
    }

    fun d(TAG: String?, message: String, e: Exception) {
        if (DEBUG) Log.d(TAG, message + e.message)
    }

    fun i(TAG: String?, message: String, e: Exception) {
        if (DEBUG) Log.i(TAG, message + e.message)
    }

    fun w(TAG: String?, message: String, e: Exception) {
        if (DEBUG) Log.w(TAG, message + e.message)
    }

    fun e(TAG: String?, message: String, e: Exception) {
        if (DEBUG) Log.e(TAG, message + e.message)
    }

    fun wtf(TAG: String?, message: String, e: Exception) {
        if (DEBUG) Log.wtf(TAG, message + e.message)
    }

    fun v(message: String, e: Exception) {
        if (DEBUG) Log.v(TAG, message + e.message)
    }

    fun d(message: String, e: Exception) {
        if (DEBUG) Log.d(TAG, message + e.message)
    }

    fun i(message: String, e: Exception) {
        if (DEBUG) Log.i(TAG, message + e.message)
    }

    fun w(message: String, e: Exception) {
        if (DEBUG) Log.w(TAG, message + e.message)
    }

    fun e(message: String, e: Exception) {
        if (DEBUG) Log.e(TAG, message + e.message)
    }

    fun wtf(message: String, e: Exception) {
        if (DEBUG) Log.wtf(TAG, message + e.message)
    }

    fun v(TAG: String?, message: String, e: Throwable) {
        if (DEBUG) Log.v(TAG, message + e.message)
    }

    fun d(TAG: String?, message: String, e: Throwable) {
        if (DEBUG) Log.d(TAG, message + e.message)
    }

    fun i(TAG: String?, message: String, e: Throwable) {
        if (DEBUG) Log.i(TAG, message + e.message)
    }

    fun w(TAG: String?, message: String, e: Throwable) {
        if (DEBUG) Log.w(TAG, message + e.message)
    }

    fun e(TAG: String?, message: String, e: Throwable) {
        if (DEBUG) Log.e(TAG, message + e.message)
    }

    fun wtf(TAG: String?, message: String, e: Throwable) {
        if (DEBUG) Log.wtf(TAG, message + e.message)
    }

    fun v(message: String, e: Throwable) {
        if (DEBUG) Log.v(TAG, message + e.message)
    }

    fun d(message: String, e: Throwable) {
        if (DEBUG) Log.d(TAG, message + e.message)
    }

    fun i(message: String, e: Throwable) {
        if (DEBUG) Log.i(TAG, message + e.message)
    }

    fun w(message: String, e: Throwable) {
        if (DEBUG) Log.w(TAG, message + e.message)
    }

    fun e(message: String, e: Throwable) {
        if (DEBUG) Log.e(TAG, message + e.message)
    }

    fun wtf(message: String, e: Throwable) {
        if (DEBUG) Log.wtf(TAG, message + e.message)
    }
}