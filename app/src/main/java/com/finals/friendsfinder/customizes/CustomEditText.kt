package com.finals.friendsfinder.customizes

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.Spannable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.finals.friendsfinder.R
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.showKeyboard
import com.tcom.vn.customview.CustomEditTextFonts
import com.tcom.vn.customview.CustomTextViewFonts

class CustomEditText : FrameLayout {
    private var edittext: CustomEditTextFonts? = null
    private var textError: CustomTextViewFonts? = null
    private var textLabel: CustomTextViewFonts? = null
    private var imageHidePassword: ImageView? = null
    private var imgDropDown: ImageView? = null
    private var imgShowPass: ImageView? = null

    private var title: String? = "Title"
    private var error: String? = "Error"

    private var lnEditText: LinearLayout? = null

    private var isHidePass = true

    constructor(context: Context) : super(context)

    var onTextChangeCallback: ((txt: String) -> Unit)? = null
    var onClickEdt: (() -> Unit)? = null

    @SuppressLint("SetTextI18n")
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        inflate(context, R.layout.layout_edittext_custom, this)
        edittext = findViewById(R.id.edittext)
        textError = findViewById(R.id.textError)
        textLabel = findViewById(R.id.textLabel)
        lnEditText = findViewById(R.id.lnEditText)
        imageHidePassword = findViewById(R.id.imageHidePassword)
        imgDropDown = findViewById(R.id.imgDropDown)

        edittext?.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotEmpty() == true) {
                textError?.visibility = View.GONE
//                edittext?.setTextColor(ContextCompat.getColor(context, R.color.color_333333))
            }
            onTextChangeCallback?.invoke(text.toString().trim())
        }
        edittext?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lnEditText?.setBackgroundResource(R.drawable.bg_blue_border_8)
            } else {
                lnEditText?.setBackgroundResource(R.drawable.bg_edt_border_8)
            }
        }
        edittext?.clickWithDebounce {
            onClickEdt?.invoke()
        }
        imgDropDown?.clickWithDebounce {
            onClickEdt?.invoke()
        }
        try {
            val a = context.obtainStyledAttributes(attributeSet, R.styleable.CustomEditText)
            title = a.getString(R.styleable.CustomEditText_label)
            error = a.getString(R.styleable.CustomEditText_error)
            textLabel?.text = title ?: ""
            textError?.text = title + error
            a.recycle()
        } catch (ex: Exception) {

        }
        setListenerImage()
    }

    fun requestFocusView(){
        edittext?.requestFocus()
        showKeyboard()
    }

    fun setInputType(type: Int) {
        edittext?.inputType = type
    }

    fun setHint(hint: String) {
        edittext?.hint = hint
    }

    fun showImagePassword(isShow: Boolean = false) {
        imageHidePassword?.isVisible = isShow
    }

    fun showImageDropDown(isShow: Boolean = false) {
        if (isShow) {
            edittext?.isClickable = true
            edittext?.isCursorVisible = false
            edittext?.isFocusable = false
        }
        imgDropDown?.isVisible = isShow
    }

    fun setMessageError(message: String) {
//        edittext?.setTextColor(ContextCompat.getColor(context, R.color.color_FF3232))
        lnEditText?.setBackgroundResource(R.drawable.bg_error_border_8)
        textError?.visibility = View.VISIBLE
        textError?.text = message
    }

    fun hideMessageError() {
        lnEditText?.setBackgroundResource(R.drawable.bg_edt_border_8)
        textError?.visibility = View.GONE
    }

    fun setLabel(label: String) {
        textLabel?.text = label
    }

    fun setHideLabel() {
        textLabel?.visibility = View.GONE
    }

    fun setLabelRequire(label: Spannable) {
        textLabel?.text = label
    }

    fun setFilterEdt(maxLength: Int) {
        edittext?.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    fun getText(): String {
        return edittext?.text.toString().trim()
    }

    fun setText(string: String) {
        edittext?.setText(string)
        lnEditText?.setBackgroundResource(R.drawable.bg_edt_border_8)
    }

    fun setMaxLength(length: Int, regexFilter: InputFilter) {
        edittext?.filters = arrayOf(InputFilter.LengthFilter(length), regexFilter)
    }

    fun setTypePassWord() {
        edittext?.transformationMethod = PasswordTransformationMethod()
    }

    private fun setListenerImage() {
        imageHidePassword?.clickWithDebounce {
            if (isHidePass) {
                edittext?.transformationMethod = null
                edittext?.setSelection(edittext?.text.toString().length)
                imageHidePassword?.setImageResource(R.drawable.ic_eye_show)
                isHidePass = false
            } else {
                edittext?.transformationMethod = PasswordTransformationMethod()
                edittext?.setSelection(edittext?.text.toString().length)
                imageHidePassword?.setImageResource(R.drawable.ic_eye_hide)
                isHidePass = true
            }
        }
    }

    fun enableInput(isEnable: Boolean = false) {
        if (isEnable) {
            edittext?.isClickable = true
            edittext?.isCursorVisible = true
            edittext?.isFocusable = true
        } else {
            edittext?.isClickable = false
            edittext?.isCursorVisible = false
            edittext?.isFocusable = false
        }
    }
}
