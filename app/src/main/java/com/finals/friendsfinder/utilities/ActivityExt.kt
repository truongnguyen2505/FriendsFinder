/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.finals.friendsfinder.utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.finals.friendsfinder.R

fun ComponentActivity.registerStartForActivityResult(onResult: (ActivityResult) -> Unit): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult(), onResult)
}

inline fun <reified AV> Activity.showActivity(
    goRoot: Boolean = false,
    enter: Int = R.anim.enter_from_right,
    exit: Int = R.anim.exit_to_left
) {
    val intent = Intent(this, AV::class.java)
    startActivity(intent)
    if (goRoot)
        finishAffinity()
    else
        finish()
    overridePendingTransition(enter, exit)
}

fun AppCompatActivity.addFragment(
    frameId: Int,
    fragment: Fragment,
    allowStateLoss: Boolean = false,
) {
    supportFragmentManager.commitTransaction(allowStateLoss) { add(frameId, fragment) }
}

fun AppCompatActivity.replaceFragment(
    frameId: Int,
    fragment: Fragment,
    tag: String? = null,
    allowStateLoss: Boolean = false,
) {
    supportFragmentManager.commitTransaction(allowStateLoss) { replace(frameId, fragment, tag) }
}

fun AppCompatActivity.replaceFragment(
    fragmentManager: FragmentManager,
    frameId: Int,
    fragment: Fragment,
    tag: String? = null,
    allowStateLoss: Boolean = false,
) {
    fragmentManager.commitTransaction(allowStateLoss) { replace(frameId, fragment, tag) }
}

fun AppCompatActivity.addFragmentToBackstack(
    frameId: Int,
    fragment: Fragment,
    tag: String? = null,
    enterAnim: Int = R.anim.enter_from_right,
    exitAnim: Int = 0,
    popEnter: Int = 0,
    popExit: Int = R.anim.exit_to_right,
    allowStateLoss: Boolean = false,
) {
    supportFragmentManager.commitTransaction(allowStateLoss) {
        setCustomAnimations(enterAnim, exitAnim, popEnter, popExit)
        add(
            frameId,
            fragment
        ).addToBackStack(tag)
    }
}

fun AppCompatActivity.addFragmentToBackstack(
    fragmentManager: FragmentManager,
    frameId: Int,
    fragment: Fragment,
    tag: String? = null,
    allowStateLoss: Boolean = false,
) {
    fragmentManager.commitTransaction(allowStateLoss) {
        add(
            frameId,
            fragment
        ).addToBackStack(tag)
    }
}

fun AppCompatActivity.popBackstack() {
    supportFragmentManager.popBackStack()
}

fun AppCompatActivity.popBackstack(fragmentManager: FragmentManager) {
    fragmentManager.popBackStack()
}

fun AppCompatActivity.popBackstack(tag: String) {
    supportFragmentManager.popBackStack(tag, 0)
}

fun AppCompatActivity.popBackstack(fragmentManager: FragmentManager, tag: String) {
    fragmentManager.popBackStack(tag, 0)
}


fun AppCompatActivity.resetBackstack() {
    repeat(supportFragmentManager.backStackEntryCount) {
        supportFragmentManager.popBackStack()
    }
}

fun AppCompatActivity.hideKeyboard() {
    currentFocus?.hideKeyboard()
}

fun Activity.restart() {
    startActivity(intent)
    finish()
}


inline fun androidx.fragment.app.FragmentManager.commitTransactionNow(func: FragmentTransaction.() -> FragmentTransaction) {
    // Could throw and make the app crash
    // e.g sharedActionViewModel.observe()
    tryOrNull("Failed to commitTransactionNow") {
        beginTransaction().func().commitNow()
    }
}

inline fun androidx.fragment.app.FragmentManager.commitTransaction(
    allowStateLoss: Boolean = false,
    func: FragmentTransaction.() -> FragmentTransaction,
) {
    val transaction = beginTransaction().func()
    if (allowStateLoss) {
        transaction.commitAllowingStateLoss()
    } else {
        transaction.commit()
    }
}


inline fun <A> tryOrNull(message: String? = null, operation: () -> A): A? {
    return try {
        operation()
    } catch (any: Throwable) {
        if (message != null) {
//            Timber.e(any, message)
        }
        null
    }
}

fun FragmentActivity.addFragmentToBackstack(
    frameId: Int,
    fragment: Fragment,
    tag: String? = null,
    enterAnim: Int = R.anim.enter_from_right,
    exitAnim: Int = 0,
    popEnter: Int = 0,
    popExit: Int = R.anim.exit_to_right,
    allowStateLoss: Boolean = false,
) {
    supportFragmentManager.commitTransaction(allowStateLoss) {
        setCustomAnimations(enterAnim, exitAnim, popEnter, popExit)
        add(
            frameId,
            fragment
        ).addToBackStack(tag)
    }
}

fun FragmentActivity.resetBackstack() {
    repeat(supportFragmentManager.backStackEntryCount) {
        supportFragmentManager.popBackStack()
    }
}

fun Fragment.hideKeyboard() {
    val view = this.activity?.currentFocus
    if (view != null) {
        view.clearFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Activity.hideKeyboard(view : View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
fun Activity.hideKeyboardActivity() {
    val view = this.currentFocus
    if (view != null) {
        view.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
fun Fragment.hideKeyboard(view: View) {
    view.clearFocus()
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}
fun Fragment.showKeyboard(view: View) {
    view.requestFocus()
    if (activity != null){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT or InputMethodManager.SHOW_FORCED)
    }
}
fun Activity.showKeyboard(view: View) {
    view.requestFocus()
    val imm =this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT or InputMethodManager.SHOW_FORCED)
}

inline fun <reified AV> FragmentActivity.showActivity(
    goRoot: Boolean = false,
    enter: Int = R.anim.enter_from_right,
    exit: Int = R.anim.exit_to_left
) {
    val intent = Intent(this, AV::class.java)
    startActivity(intent)
    if (goRoot)
        finishAffinity()
    else
        finish()
    overridePendingTransition(enter, exit)
}
