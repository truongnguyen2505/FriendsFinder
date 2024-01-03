package com.finals.friendsfinder.customizes

import android.content.Context
import android.graphics.Typeface

object FontCache {
    private val fontCache = HashMap<String, Typeface?>()
    @JvmStatic
    fun getTypeface(context: Context, fontName: String): Typeface? {
            var typeface = fontCache[fontName]
        if (typeface == null) {
            typeface = try {
                Typeface.createFromAsset(context.assets, fontName)
            } catch (e: Exception) {
                try {
                    Typeface.createFromAsset(context.assets, "$fontName.otf")
                } catch (e1: Exception) {
                    try {
                        Typeface.createFromAsset(context.assets, "$fontName.ttf")
                    } catch (e2: Exception) {
                        return null
                    }
                }
            }
            fontCache[fontName] = typeface
        }
        return typeface
    }
}
