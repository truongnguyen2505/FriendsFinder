package com.finals.friendsfinder.utilities

fun Int.convertPxToDp(): Int {
    return this * Utils.shared.dpiDevice / 160
}

fun Float.convertPxToDp(): Float {
    return this * Utils.shared.dpiDevice / 160.0f
}

fun Int.convertDpToPx(): Int {
    return this * 160 / Utils.shared.dpiDevice
}

fun Float.convertDpToPx(): Float {
    return 1.0f * (this * 160.0f / Utils.shared.dpiDevice)
}
