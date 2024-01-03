package com.finals.friendsfinder.bases

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.viewbinding.ViewBinding
import com.finals.friendsfinder.utilities.Utils


public abstract class BaseDialog<VB : ViewBinding>(context: Context, var themeId: Int) :
    Dialog(context, themeId), View.OnTouchListener, DialogInterface.OnDismissListener {

    open lateinit var rootView: VB

    internal var mContext: Context? = context
    private val isHideKeyboardOnStartUp: Boolean
        get() = true

    fun setHeader(context: Context?) {
        val window = this.window ?: return
        val flag = (context as Activity?)!!.window.attributes.flags
        window.setFlags(flag, flag)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        )
    }

    abstract fun foundView()

    abstract fun notFoundView()

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        hideKeyboard()
        return false
    }

    fun hideKeyboard() {
        val view: View? = this.currentFocus
        if ((view != null) && (mContext != null) && (view !is EditText)) {
            (mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                view.windowToken,
                0
            )
            view.clearFocus()
        }
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        try {
            if (Utils.shared.checkKeyboardVisible())
                ownerActivity?.let { Utils.shared.showHideKeyBoard(it, false) }
        } catch (ignored: Exception) {
        }
    }

    abstract fun getViewBinding(): VB

    init {
        //        val window: Window =
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.setStatusBarColor(Color.parseColor("#EBF0F7"))
        window?.setWindowAnimations(themeId)
        val view: View
        try {
            rootView = getViewBinding()
            view = rootView!!.root
            setHeader(context)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(view)
            view.setOnTouchListener(this)
            view.isClickable = true
            view.isFocusableInTouchMode = true
            foundView()
            if (isHideKeyboardOnStartUp) {
                hideKeyboard()
            }
        } catch (e: Exception) {
            notFoundView()
        }
    }
}
