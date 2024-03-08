package com.finals.friendsfinder.views.home

import android.Manifest
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentMyQrBinding
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.QrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBackground
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColors
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogo
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoPadding
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorShapes
import java.io.File


class MyQRFragment : BaseFragment<FragmentMyQrBinding>() {

    companion object {
        fun newInstance(): MyQRFragment {
            val arg = Bundle().apply {
            }
            return MyQRFragment().apply {
                arguments = arg
            }
        }
    }

    private val readPermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var mUri: Uri? = null
    private var mFile: File? = null

    override fun setupView() {
        super.setupView()
        val userInfo = Utils.shared.getUser()
        val phone = userInfo?.phoneNumber ?: ""
        val data = QrData.Text(phone)
        val options = QrVectorOptions.Builder()
            .setPadding(.3f)
            .setLogo(
                QrVectorLogo(
                    drawable = ContextCompat
                        .getDrawable(requireContext(), R.drawable.ic_avatar_empty_25),
                    size = .25f,
                    padding = QrVectorLogoPadding.Natural(.2f),
                    shape = QrVectorLogoShape
                        .Circle
                )
            )
            .setBackground(
                QrVectorBackground(
                    drawable = ContextCompat
                        .getDrawable(requireContext(), R.drawable.bg_for_qr_code),
                )
            )
            .setColors(
                QrVectorColors(
                    dark = QrVectorColor
                        .Solid(ContextCompat.getColor(requireContext(), R.color.color_56738A)),
                    ball = QrVectorColor.Solid(
                        ContextCompat.getColor(requireContext(), R.color.color_56738A)
                    ),
                    frame = QrVectorColor.LinearGradient(
                        colors = listOf(
                            0f to ContextCompat.getColor(requireContext(), R.color.color_56738A),
                            1f to ContextCompat.getColor(requireContext(), R.color.color_56738A),
                        ),
                        orientation = QrVectorColor.LinearGradient
                            .Orientation.LeftDiagonal
                    )
                )
            )
            .setShapes(
                QrVectorShapes(
                    darkPixel = QrVectorPixelShape
                        .RoundCorners(.5f),
                    ball = QrVectorBallShape
                        .RoundCorners(.10f),
                    frame = QrVectorFrameShape
                        .RoundCorners(.10f),
                )
            )
            .build()

        val drawable: Drawable = QrCodeDrawable(data, options)
        Glide.with(requireContext()).load(drawable).into(rootView.imgQR)
    }

    override fun bindData() {
        super.bindData()
        with(rootView) {
            btnDownload.clickWithDebounce {
                if (mUri != null || mFile != null){
                    ToastUtils.showShort("This photo has been downloaded!")
                    return@clickWithDebounce
                }
                val bitmap = Utils.shared.getBitmapFromNestedScrollView(
                    requireContext(),
                    rootView.nestedScrollView
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Utils.shared.saveBitmapImage(
                        requireContext(),
                        bitmap,
                        inSuccess = { uri, file ->
                            mUri = uri
                            mFile = file
                            if (uri != null || file != null) {
                                ToastUtils.showShort("Download image successfully!")
                            } else {
                                ToastUtils.showShort("Download image fail!")
                            }
                            //Log.d("TAG", "saveBitmapImage: save success $uri nnnnn $file")
                        })
                } else {
                    PermissionUtils.permission(*readPermissions)
                        .callback(object : PermissionUtils.SimpleCallback {
                            override fun onGranted() {
                                Utils.shared.saveBitmapImage(
                                    requireContext(),
                                    bitmap,
                                    inSuccess = { uri, file ->
                                        mUri = uri
                                        mFile = file
                                        if (uri != null || file != null) {
                                            ToastUtils.showShort("Download image successfully!")
                                        } else {
                                            ToastUtils.showShort("Download image fail!")
                                        }
                                    })
                            }

                            override fun onDenied() {
                            }
                        }).request()
                }
            }
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
            layoutHeader.tvMessage.text = "My QR"
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentMyQrBinding {
        return FragmentMyQrBinding.inflate(inflater)
    }
}