package com.tcom.vn.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.finals.friendsfinder.R
import com.finals.friendsfinder.customizes.FontCache
import com.finals.friendsfinder.utilities.commons.Constants


class CustomEditTextFonts : AppCompatEditText {

    var curColor: Int = Color.BLACK
    var typeFont: String = Constants.DEFAULT_FONT

    constructor(context: Context) : super(context)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        curColor = currentTextColor
        try {
            val a =
                context.obtainStyledAttributes(attributeSet, R.styleable.CustomTextView, 0, 0)
            typeFont = a.getString(R.styleable.CustomTextView_font_type)!!
            typeface = Typeface.createFromAsset(context.assets, typeFont)
            a.recycle()
        } catch (ex: Exception) {
            typeface = try {
                Typeface.createFromAsset(context.assets, Constants.DEFAULT_FONT)
            } catch (ex1: Exception) {
                Typeface.DEFAULT
            }
        }
    }

    fun setTypeFont(context: Context, typeFont: String) {
        this.typeFont = typeFont
        this.typeface = FontCache.getTypeface(context, typeFont)
    }

    @JvmName("getTypeFont1")
    fun getTypeFont(): String {
        return typeFont
    }
}