package com.finals.friendsfinder.bases

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.finals.friendsfinder.R
import com.finals.friendsfinder.customizes.FontCache
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.models.EventChangeLanguage
import com.finals.friendsfinder.utilities.UserDefaults
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.MessageStatus
import com.finals.friendsfinder.utilities.setViewClickListener
import com.finals.friendsfinder.views.home.MainActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var snackbar: Snackbar? = null
    open lateinit var rootView: VB

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = getViewBinding()
        BarUtils.setStatusBarVisibility(this, false)
        setContentView(rootView.root)

        EventBus.getDefault().register(this)

        observeHandle()

        setupView()

        setupEventControl()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun subscribeEventLanguage(eventLanguage: EventChangeLanguage) {
        if (eventLanguage.event == Constants.EVENT_CHANGE_LANGUAGE)
            onLanguageChanged()
    }

    open fun onLanguageChanged() {

    }

    open fun setupEventControl() {

    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 1)
            showMain()
        super.onBackPressed()
    }

    open fun showMain(){}

    @SuppressLint("ClickableViewAccessibility")
    open fun setupView() {
        rootView.root.setOnTouchListener { view, motionEvent ->
            if ((motionEvent.action == MotionEvent.ACTION_UP) && (view !is EditText))
                if (Utils.shared.checkKeyboardVisible()) {
                    Utils.shared.showHideKeyBoard(this, false)
                }
            return@setOnTouchListener true
        }
    }

    open fun observeHandle() {

    }

    protected abstract fun getViewBinding(): VB

    @SuppressLint("UseRequireInsteadOfGet")
    fun showSnackBar(
        status: MessageStatus,
        message: String = "",
        isInfinitive: Boolean = false
    ): Snackbar {
        val duration = if (isInfinitive)
            Snackbar.LENGTH_INDEFINITE
        else {
            snackbar?.dismiss()
            snackbar = null
            Snackbar.LENGTH_SHORT
        }
        if (snackbar == null)
            snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                message,
                duration
            )
        if (isInfinitive)
            snackbar?.behavior = NoSwipeBehavior()

        val sbView = snackbar?.view
        val layoutParams: FrameLayout.LayoutParams =
            sbView?.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        when (status) {
            MessageStatus.ERROR -> {
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.red_400))
            }
            MessageStatus.SUCCESS -> {
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.color_snackbar_success
                    )
                )
            }
            MessageStatus.INFO -> {
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.color_snackbar_info
                    )
                )
            }
            MessageStatus.WARNING -> {
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.color_snackbar_warning
                    )
                )
            }
        }
        sbView.layoutParams = layoutParams
        val textView: TextView? =
            sbView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView?.setPadding(0, 2, 0, 2)
        textView?.gravity = Gravity.CENTER
        textView?.typeface = FontCache.getTypeface(this, Constants.DEFAULT_FONT)
        textView?.setTextColor(ContextCompat.getColor(this, R.color.white))
        if (!isInfinitive)
            textView?.setViewClickListener { snackbar?.dismiss() }
        else
            snackbar?.setText(message)
        if (snackbar?.isShown == false)
            snackbar?.show()
        return snackbar!!
    }

    open fun showMessage(
        title: String = "",
        message: String = "",
    ) {
        NotifyDialog.instance(this)
            .setTitle(title)
            .setMessage(message)
            .enableCancel(false)
            .show()
    }

    open fun showMessage(
        title: String = "",
        message: String = "",
        listener: NotifyDialog.OnDialogListener?,
    ) {
        NotifyDialog.instance(this)
            .setTitle(title)
            .setListener(listener)
            .setMessage(message)
            .enableCancel(false)
            .show()
    }

    open fun showQuestion(
        title: String = "",
        message: String = "",
    ) {
        NotifyDialog.instance(this)
            .setTitle(title)
            .setMessage(message)
            .show()
    }

    open fun showQuestion(
        title: String = "",
        message: String = "",
        listener: NotifyDialog.OnDialogListener?,
    ) {
        NotifyDialog.instance(this)
            .setTitle(title)
            .setListener(listener)
            .setMessage(message)
            .show()
    }

    private fun showSettingApp() {
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:$packageName")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(i)
    }

    //endregion

    //region Event for change language
    fun setLanguage(language: String) {
        UserDefaults.standard.setSharedPreference(
            Constants.CURRENT_LANGUAGE,
            language
        )
        Locale.setDefault(currentLanguage)
        val config = Configuration()
        config.locale = currentLanguage
        resources.updateConfiguration(config, null)
        EventBus.getDefault()
            .post(EventChangeLanguage(Constants.EVENT_CHANGE_LANGUAGE, "lang"))
    }

    val currentLanguage: Locale
        get() {
            val currentLanguage = UserDefaults.standard.getSharedPreference(
                Constants.CURRENT_LANGUAGE,
                "en"
            ) as? String ?: "en"
            return Locale(currentLanguage)
        }

}

internal class NoSwipeBehavior : BaseTransientBottomBar.Behavior() {
    override fun canSwipeDismissView(child: View): Boolean {
        return false
    }
}