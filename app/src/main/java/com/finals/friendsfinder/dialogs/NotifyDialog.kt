package com.finals.friendsfinder.dialogs

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.finals.friendsfinder.bases.BaseDialog
import com.finals.friendsfinder.databinding.DialogNotifyBinding
import com.finals.friendsfinder.utilities.setViewClickListener


class NotifyDialog private constructor(context: Context) :
    BaseDialog<DialogNotifyBinding>(context, R.style.Theme_Translucent_NoTitleBar) {
    private var mListener: OnDialogListener? = null
    fun setListener(listener: OnDialogListener?): NotifyDialog {
        mListener = listener
        return this
    }

    override fun foundView() {
        rootView.btnOK.setViewClickListener { clickOk() }
        rootView.btnCancel.setViewClickListener { clickCancel() }
    }

    fun setTitle(title: String?): NotifyDialog {
        rootView.tvTitle.text = title
        rootView.tvTitle.isVisible = !title.isNullOrEmpty()
        return this
    }

    fun setMessage(message: String?): NotifyDialog {
        rootView.tvMessage.text = message
        rootView.tvMessage.isVisible = !message.isNullOrEmpty()
        return this
    }


    fun setTextBtnOk(btnOk: String?): NotifyDialog {
        rootView.btnOK.text = btnOk
        return this
    }


    override fun notFoundView() {

    }

    override fun getViewBinding(): DialogNotifyBinding {
        return DialogNotifyBinding.inflate(layoutInflater)
    }

    fun enableOk(isEnable: Boolean): NotifyDialog {
        if (isEnable) rootView.btnOK.visibility = View.VISIBLE else rootView.btnOK.visibility =
            View.GONE
        return this
    }

    fun enableCancel(isEnable: Boolean): NotifyDialog {
        if (isEnable) rootView.btnCancel.visibility =
            View.VISIBLE else rootView.btnCancel.visibility =
            View.GONE
        return this
    }

    fun clickOk() {
        dismiss()
        if (mListener != null) mListener!!.onClickButton(true)
    }

    fun clickCancel() {
        dismiss()
        if (mListener != null) mListener!!.onClickButton(false)
    }

    interface OnDialogListener {
        fun onClickButton(isOk: Boolean)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mNotifyDialog: NotifyDialog? = null

        fun instance(context: Context): NotifyDialog {
            if (mNotifyDialog != null) {
                mNotifyDialog!!.dismiss()
                mNotifyDialog = null
            }
            mNotifyDialog = NotifyDialog(context)
            return mNotifyDialog!!
        }
    }
}
