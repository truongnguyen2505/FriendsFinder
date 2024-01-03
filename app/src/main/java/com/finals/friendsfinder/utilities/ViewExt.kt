package com.finals.friendsfinder.utilities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.getSystemService
import com.finals.friendsfinder.R


//region setting animation for view
fun View.fadeOut(alpha: Float = 0.0f, duration: Long = 1000) {
    animate()
        .alpha(alpha)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
            }
        })
}

fun View.fadeIn(alpha: Float = 1.0f, duration: Long = 1000) {
    setAlpha(0.0f)
    visibility = View.VISIBLE
    animate()
        .alpha(alpha).duration = duration
}

fun View.enterFromBottom(duration: Long = 300) {
    val animate = TranslateAnimation(
        0.0f,  // fromXDelta
        0.0f,  // toXDelta
        height.toFloat(),  // fromYDelta
        0.0f
    ) // toYDelta

    animate.duration = duration
    animate.fillAfter = true
    startAnimation(animate)
}

fun View.exitToBottom(duration: Long = 300, onFinish: (() -> Unit)) {
    val animate = TranslateAnimation(
        0.0f,  // fromXDelta
        0.0f,  // toXDelta
        0.0f,  // fromYDelta
        height.toFloat()
    ) // toYDelta

    animate.duration = duration
    animate.fillAfter = true
    animate.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(p0: Animation?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            onFinish()
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }
    })
    startAnimation(animate)
}

fun ImageView.changeResourceAnim(image: Int) {
    val animOut: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    val animIn: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    animOut.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            setImageResource(image)
            animIn.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {}
            })
            startAnimation(animIn)
        }
    })
    startAnimation(animOut)
}

fun TextView.changeTextColorAnim(toColor: Int) {
    val animOut: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    val animIn: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    animOut.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            setTextColor(toColor)
            animIn.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {}
            })
            startAnimation(animIn)
        }
    })
    startAnimation(animOut)
}

fun View.performClick() {
    val SCALE_VAL = 0.85f
    val DURATION_ANIM: Long = 500
    animate()
        .scaleX(SCALE_VAL)
        .scaleY(SCALE_VAL)
        .setDuration(DURATION_ANIM)
        .setInterpolator(LinearInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                performClick()
            }

            override fun onAnimationCancel(p0: Animator) {
                performClick()
            }

            override fun onAnimationRepeat(p0: Animator) {

            }
        }).start()

}

fun View.setViewClickListener(listener: View.OnClickListener?) {
    val SCALE_VAL = 0.85f
    val DURATION_ANIM: Long = 150
    fun reverseScaleView(view: View) {
        animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(DURATION_ANIM)
            .setInterpolator(LinearInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    listener?.onClick(view)
                }

                override fun onAnimationCancel(p0: Animator) {
                    listener?.onClick(view)
                }

                override fun onAnimationRepeat(p0: Animator) {

                }
            }).start()

    }

    setOnClickListener {
        animate()
            .scaleX(SCALE_VAL)
            .scaleY(SCALE_VAL)
            .setDuration(DURATION_ANIM)
            .setInterpolator(LinearInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}

                override fun onAnimationEnd(p0: Animator) {
                    reverseScaleView(it)
                }

                override fun onAnimationCancel(p0: Animator) {
                    reverseScaleView(it)
                }

                override fun onAnimationRepeat(p0: Animator) {}
            }).start()

    }
}

//endregion


fun View.hideKeyboard() {
    val imm = context?.getSystemService<InputMethodManager>()
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard(andRequestFocus: Boolean = false) {
    if (andRequestFocus) {
        requestFocus()
    }
    val imm = context?.getSystemService<InputMethodManager>()
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}
