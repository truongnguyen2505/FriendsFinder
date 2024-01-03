package com.finals.friendsfinder.dialogs

import android.content.Context
import android.os.Environment
import androidx.core.view.isVisible
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseDialog
import com.finals.friendsfinder.databinding.DialogPickerImageBinding
import com.finals.friendsfinder.utilities.*
import java.io.File
import java.util.*

class DialogChoiseImage(context: Context) :
    BaseDialog<DialogPickerImageBinding>(context, R.style.CustomDialogBottom) {
    private var outputPath: String = ""
    var onPickCamera: ((outputPath: String) -> Unit)? = null
    var onPickGalery: (() -> Unit)? = null

    override fun foundView() {
        rootView.viewDimmer.isVisible = true
        rootView.layoutMain.isVisible = true
        rootView.layoutMain.post {
            rootView.viewDimmer.fadeIn()
            rootView.layoutMain.enterFromBottom()
        }
        outputPath = ""
        rootView.viewDimmer.setOnClickListener {
            dismiss()
        }
        rootView.btnCancel.setViewClickListener {
            dismiss()
        }
        rootView.txtFromCamera.setViewClickListener {
            outputPath = createImageFile()
            onPickCamera?.invoke(outputPath)
            dismiss()
        }
        rootView.txtFromGalery.setViewClickListener {
            outputPath = ""
            onPickGalery?.invoke()
            dismiss()
        }
    }

    private fun createImageFile(): String {
        // Create an image file name
        val storageDir: File? = mContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return if (storageDir != null)
            File.createTempFile(
                "JPEG_${Date().time}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).absolutePath
        else
            ""
    }

    override fun dismiss() {
        rootView.viewDimmer.fadeOut()
        rootView.layoutMain.exitToBottom(onFinish = {
            super.dismiss()
        })
    }

    override fun notFoundView() {

    }

    override fun getViewBinding(): DialogPickerImageBinding {
        return DialogPickerImageBinding.inflate(layoutInflater)
    }

}