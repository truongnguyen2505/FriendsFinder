package com.finals.friendsfinder.utilities

import androidx.databinding.BindingAdapter
import com.finals.friendsfinder.customizes.CustomTextViewFonts

object TextViewExt {

    @BindingAdapter("app:resourceId")
    @JvmStatic
    fun setText(view: CustomTextViewFonts, resourceId: Int) {
        view.text = view.context.resources.getString(resourceId)
    }

}
