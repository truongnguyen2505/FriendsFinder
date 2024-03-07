package com.finals.friendsfinder.bases

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.finals.friendsfinder.R
import com.finals.friendsfinder.customizes.FontCache
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.models.EventChangeLanguage
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.MessageStatus
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.setViewClickListener
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

public abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    open lateinit var rootView: VB

    companion object {
//        const val TAG = <T: BaseFragment>::class
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun subscribeEventLanguage(eventLanguage: EventChangeLanguage) {
        if (eventLanguage.event.equals(Constants.EVENT_CHANGE_LANGUAGE))
            onLanguageChanged()
    }

    open fun onLanguageChanged() {

    }

    open fun bindData() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        rootView = getViewBinding(inflater)

        observeHandle()

        setupView()

        setupEventControl()
        return rootView.root
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
        enableCancel: Boolean = false,
        listener: NotifyDialog.OnDialogListener?,
    ) {
        context?.let {
            NotifyDialog.instance(it)
                .setTitle(title)
                .setListener(listener)
                .setMessage(message)
                .enableCancel(enableCancel)
                .show()
        }
    }

    open fun showMessage(
        title: String = "",
        message: String = "",
        txtBtnOk: String = "",
        enableCancel: Boolean = false,
        listener: NotifyDialog.OnDialogListener?,
    ) {
        context?.let {
            NotifyDialog.instance(it)
                .setTitle(title)
                .setListener(listener)
                .setMessage(message)
                .enableCancel(enableCancel)
                .setTextBtnOk(txtBtnOk)
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
