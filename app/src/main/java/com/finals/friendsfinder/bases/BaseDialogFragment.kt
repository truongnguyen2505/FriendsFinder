package com.finals.friendsfinder.bases

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.finals.friendsfinder.R
import com.finals.friendsfinder.customizes.FontCache
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.MessageStatus
import com.finals.friendsfinder.utilities.setViewClickListener
import com.google.android.material.snackbar.Snackbar

public abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment(),
    View.OnTouchListener {

    open lateinit var rootView: VB
    private val isHideKeyboardOnStartUp: Boolean
        get() = true

    fun setHeader(context: Context?, dialog: Dialog) {
        val window = dialog.window ?: return
        val flag = (context as Activity?)!!.window.attributes.flags
        window.setFlags(flag, flag)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
    }

    abstract fun foundView()

    abstract fun notFoundView()

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        hideKeyboard()
        return false
    }

    fun hideKeyboard() {
        val view: View? = this.dialog?.currentFocus
        if ((view != null) && (context != null) && (view !is EditText)) {
            (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                view.windowToken,
                0
            )
            view.clearFocus()
        }
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        try {
            if (Utils.shared.checkKeyboardVisible())
                activity?.let { Utils.shared.showHideKeyBoard(it, false) }
        } catch (ignored: Exception) {
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, android.R.style.Theme_Light)
        dialog.window?.setWindowAnimations(R.style.CustomDialogBottom)
        val view: View
        try {
            setHeader(context, dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog.window?.addFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )  // the content
            val root = RelativeLayout(activity)
            root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog.setContentView(root)
            foundView()
        } catch (e: Exception) {
            notFoundView()
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = getViewBinding(inflater)
        val view = rootView.root
        view.setOnTouchListener(this)
        observeHandle()
        setupView()
        setupEventControl()
        view.isClickable = true
        view.isFocusableInTouchMode = true
        if (isHideKeyboardOnStartUp) {
            hideKeyboard()
        }
        return rootView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
    }

    open fun bindData() {

    }

    open fun setupEventControl() {

    }

    @SuppressLint("ClickableViewAccessibility")
    open fun setupView() {
        rootView.root.setOnTouchListener { view, motionEvent ->
            if ((motionEvent.action == MotionEvent.ACTION_UP) && !(view is EditText))
                activity?.let { Utils.shared.showHideKeyBoard(it, false) }
            return@setOnTouchListener true
        }
    }

    open fun observeHandle() {

    }

    protected abstract fun getViewBinding(inflater: LayoutInflater): VB

    @SuppressLint("UseRequireInsteadOfGet")
    fun showSnackBar(
        status: MessageStatus, message: String = "",
        isInfinitive: Boolean = false
    ): Snackbar {
        val duration = if (isInfinitive)
            Snackbar.LENGTH_INDEFINITE
        else
            Snackbar.LENGTH_SHORT
        val snackbar = Snackbar.make(
            activity!!.findViewById(android.R.id.content),
            message,
            duration
        )
        val sbView = snackbar.view
        val layoutParams: FrameLayout.LayoutParams = sbView.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        when (status) {
            MessageStatus.ERROR -> {
                sbView.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.red_400))
            }
            MessageStatus.SUCCESS -> {
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_snackbar_success
                    )
                )
            }
            MessageStatus.INFO -> {
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_snackbar_info
                    )
                )
            }
            MessageStatus.WARNING -> {
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_snackbar_warning
                    )
                )
            }
        }
        sbView.layoutParams = layoutParams
        val textView: TextView = sbView
            .findViewById(com.google.android.material.R.id.snackbar_text)
        textView.setPadding(0, 2, 0, 2)
        textView.gravity = Gravity.CENTER
        textView.typeface = FontCache.getTypeface(activity!!, Constants.DEFAULT_FONT)
        textView.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        textView.setViewClickListener { snackbar.dismiss() }
        snackbar.show()
        return snackbar
    }

    open fun showMessage(
        title: String = "",
        message: String = "",
    ) {
        context?.let {
            NotifyDialog.instance(it)
                .setTitle(title)
                .setMessage(message)
                .enableCancel(false)
                .show()
        }
    }

    open fun showMessage(
        title: String = "",
        message: String = "",
        listener: NotifyDialog.OnDialogListener?,
    ) {
        context?.let {
            NotifyDialog.instance(it)
                .setTitle(title)
                .setListener(listener)
                .setMessage(message)
                .enableCancel(false)
                .show()
        }
    }

    open fun showQuestion(
        title: String = "",
        message: String = "",
    ) {
        context?.let {
            NotifyDialog.instance(it)
                .setTitle(title)
                .setMessage(message)
                .show()
        }
    }

    open fun showQuestion(
        title: String = "",
        message: String = "",
        listener: NotifyDialog.OnDialogListener?,
    ) {
        context?.let {
            NotifyDialog.instance(it)
                .setTitle(title)
                .setListener(listener)
                .setMessage(message)
                .show()
        }
    }

}
