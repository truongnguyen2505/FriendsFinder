package com.finals.friendsfinder.customizes

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import com.finals.friendsfinder.R
import com.finals.friendsfinder.databinding.DialogCustomLoadingBinding


class LoadingDialog {

    companion object {

        private var dialog: CustomDialog? = null

        fun show(context: Context): Dialog {
            return show(context, "")
        }

        fun show(context: Context, title: String = ""): Dialog {
            if (isShowing())
                dismiss()
            val inflater = (context as Activity).layoutInflater
            val binding = DialogCustomLoadingBinding.inflate(inflater)
            binding.txtMessage.text = title

            // Progress Bar Color
            setColorFilter(
                binding.progressBar.indeterminateDrawable,
                ResourcesCompat.getColor(context.resources, R.color.color_btn_blue, null)
            )

            dialog = CustomDialog(context)
            dialog?.setContentView(binding.root)
            run {
                dialog?.show()
            }
            return dialog!!
        }

        fun dismiss() {
            try {
//                if (isShowing())
                dialog?.dismiss()
            } catch (e: Exception) {
            }
            dialog = null
        }

        fun isShowing(): Boolean {
            return dialog?.isShowing == true
        }

        private fun setColorFilter(drawable: Drawable, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
            } else {
                @Suppress("DEPRECATION")
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }


    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.transparency)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}