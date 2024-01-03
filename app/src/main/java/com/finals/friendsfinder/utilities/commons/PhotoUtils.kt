package com.finals.friendsfinder.utilities.commons

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.finals.friendsfinder.utilities.Log
import java.io.File
import java.io.IOException

class PhotoUtils {

    companion object {
        val shared = PhotoUtils()
        private const val AUTHORITY: String = "com.tcom.mobile.authentication.fileprovider"
        private const val REQUEST_CODE_CAMERA = 10001
        private const val REQUEST_CODE_GALERY = 10002
    }

    private var outputPath: String = ""
    var onPickImageResult: ((bitmap: Bitmap?, message: String) -> Unit)? = null

    fun fromCamera(
        fragment: Fragment,
        outputPath: String,
        callback: ((bitmap: Bitmap?, message: String) -> Unit)? = null
    ) {
        Log.i("camera", "startCameraActivity()")
        this.outputPath = outputPath
        this.onPickImageResult = callback
        val file = File(outputPath)
        val outputFileUri: Uri = FileProvider.getUriForFile(
            fragment.requireContext(),
            AUTHORITY,
            file
        )
        val intent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        fragment.startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    fun fromCard(
        fragment: Fragment,
        callback: ((bitmap: Bitmap?, message: String) -> Unit)? = null
    ) {
        this.outputPath = ""
        this.onPickImageResult = callback
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        fragment.startActivityForResult(i, REQUEST_CODE_GALERY)
    }

    fun onActivityResult(fragment: Fragment, requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALERY -> {
                    if (data != null)
                        onPhotoPick(fragment, data)
                }
                REQUEST_CODE_CAMERA -> {
                    Log.i("SonaSys", "resultCode: $resultCode")
                    onPhotoTaken()
                }
            }
        }
    }

    private fun onPhotoPick(fragment: Fragment, data: Intent) {
        val selectedImage: Uri? = data.data
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = selectedImage?.let {
            fragment.activity?.contentResolver?.query(
                it,
                filePathColumn, null, null, null
            )
        }
        cursor?.moveToFirst()
        val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
        val picturePath: String? = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        val bitmap: Bitmap? = picturePath?.let { BitmapFactory.decodeFile(it) }
        if (bitmap == null)
            onPickImageResult?.invoke(null, "Cannot get image!")
        else
            onPickImageResult?.invoke(bitmap, "Get image successfully!")
    }

    private fun onPhotoTaken() {
        // Log message
        Log.i("SonaSys", "onPhotoTaken")
        if (outputPath.isEmpty()) {
            onPickImageResult?.invoke(null, "Cannot get image!")
        } else {
            val bitmap = convertImageToPortrait()
            deleteFile(outputPath)
            if (bitmap == null)
                onPickImageResult?.invoke(null, "Cannot get image!")
            else
                onPickImageResult?.invoke(bitmap, "Get image successfully!")

        }
    }

    private fun convertImageToPortrait(): Bitmap? {
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(outputPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val orientString: String? = exif?.getAttribute(ExifInterface.TAG_ORIENTATION)
        val orientation = orientString?.toInt() ?: ExifInterface.ORIENTATION_NORMAL
        var rotationAngle: Float = 0.0f

        // Rotate Bitmap
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1.0f, 1.0f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotationAngle = 180.0f
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                rotationAngle = 180.0f
                matrix.postScale(-1.0f, 1.0f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                rotationAngle = 90.0f
                matrix.postScale(-1.0f, 1.0f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> rotationAngle = 90.0f
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                rotationAngle = -90.0f
                matrix.postScale(-1.0f, 1.0f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> rotationAngle = -90.0f
            ExifInterface.ORIENTATION_NORMAL -> {}
            else -> {}
        }
        matrix.setRotate(rotationAngle)
        return try {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inSampleSize = 4

            val bitmap = BitmapFactory.decodeFile(outputPath, options)
            Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true)
        } catch (error: Exception) {
            error.printStackTrace()
            null
        }
    }

    fun deleteFile(filePath: String?) {
        try {
            val file = File(filePath.toString())
            if (file.exists()) {
                if (file.listFiles().isNullOrEmpty()) {
                    file.delete()
                } else {
                    file.listFiles().forEach { fileChild ->
                        deleteFile(fileChild.absolutePath)
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}