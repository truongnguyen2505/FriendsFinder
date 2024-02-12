package com.finals.friendsfinder.customizes

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.finals.friendsfinder.R
import com.finals.friendsfinder.utilities.commons.Constants


class CustomTextViewFonts : AppCompatTextView {

    var curColor: Int = Color.BLACK
    var typeFont: String = Constants.DEFAULT_FONT

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        curColor = currentTextColor
        try {
            val a =
                context.obtainStyledAttributes(attributeSet, R.styleable.CustomTextView, 0, 0)
            typeFont = a.getString(R.styleable.CustomTextView_font_type)!!
            typeface = FontCache.getTypeface(context, typeFont)
            a.recycle()
        } catch (ex: Exception) {
            typeface = try {
                FontCache.getTypeface(context, Constants.DEFAULT_FONT)
            } catch (ex1: Exception) {
                (Typeface.DEFAULT)
            }
        }
    }

    fun setTypeFontByName(context: Context, fontName: String?) {
        val fontFile = String.format("fonts/%s", fontName)
        setTypeFont(context, fontFile)
    }

    fun setTypeFont(context: Context, typeFont: String) {
        this.typeFont = typeFont
        typeface = FontCache.getTypeface(context, typeFont)
    }

}

