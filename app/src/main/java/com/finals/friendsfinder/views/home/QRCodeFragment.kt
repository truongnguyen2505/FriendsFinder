package com.finals.friendsfinder.views.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentQrCodeBinding
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.permission.PERMISSIONS_FOR_TAKING_PHOTO
import com.finals.friendsfinder.utilities.permission.checkPermissions
import com.finals.friendsfinder.utilities.permission.registerForPermissionsResult
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QRCodeFragment : BaseFragment<FragmentQrCodeBinding>(), ZXingScannerView.ResultHandler {
    companion object {
        private const val TAG = "QRCodeFragment"
        private const val USER_LIST = "USER_LIST"
        fun newInstance(listUser: ArrayList<UserInfo?>): QRCodeFragment {
            val arg = Bundle().apply {
                putParcelableArrayList(USER_LIST, listUser)
            }
            return QRCodeFragment().apply {
                arguments = arg
            }
        }
    }

    private var listUser: MutableList<UserInfo> = mutableListOf()
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val openCameraActivityResultLauncher = registerForPermissionsResult { allGranted, _ ->
        if (allGranted) {
            startCamera()
        }
    }
    private var startCameraRunnable = Runnable {
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        rootView.userCodeScannerView.setResultHandler(null)
        rootView.userCodeScannerView.stopCamera()
        handler.removeCallbacks(startCameraRunnable)
    }

    override fun onResume() {
        super.onResume()
        rootView.userCodeScannerView.setResultHandler(this)
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) handler.postDelayed(startCameraRunnable, 500)

    }

    override fun onStart() {
        super.onStart()

        if (checkPermissions(
                PERMISSIONS_FOR_TAKING_PHOTO, requireActivity(), openCameraActivityResultLauncher
            )
        ) {
            handler.postDelayed(startCameraRunnable, 500)
        }
    }

    private fun startCamera() {
        rootView.userCodeScannerView.startCamera()
    }

    override fun observeHandle() {
        super.observeHandle()
        getArg()
    }

    private fun getArg() {
        arguments?.let {
            listUser = it.getParcelableArrayList(USER_LIST) ?: arrayListOf()
        }
    }

    override fun setupView() {
        super.setupView()
    }

    override fun bindData() {
        super.bindData()
        with(rootView) {
            btnBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
            btnMyQr.clickWithDebounce {
                activity?.addFragmentToBackstack(android.R.id.content, MyQRFragment.newInstance())
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentQrCodeBinding {
        return FragmentQrCodeBinding.inflate(inflater)
    }

    override fun handleResult(rawResult: Result?) {
        if (rawResult == null) {
            showMessage("No data!")
        } else {
            val rawBytes = Utils.shared.getRawBytes(rawResult)
            val rawBytesStr = rawBytes?.toString(Charsets.ISO_8859_1)
            val value = rawBytesStr ?: rawResult.text
            if (TextUtils.isEmpty(value)) {
                //if txt empty
                showMessage(
                    "Information",
                    "This QR is wrong! Please, try again!",
                    listener = object : NotifyDialog.OnDialogListener {
                        override fun onClickButton(isOk: Boolean) {
                            if (isOk) {
                                rootView.userCodeScannerView.resumeCameraPreview(this@QRCodeFragment)
                            }
                        }
                    })
                return
            }
            //do some success here
            Log.d(TAG, "handleResult: $value")
            rootView.userCodeScannerView.resumeCameraPreview(this)
        }
    }
}