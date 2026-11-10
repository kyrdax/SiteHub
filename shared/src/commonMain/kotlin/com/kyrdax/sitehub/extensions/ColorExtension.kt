package com.kyrdax.sitehub.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.toHexCode(): String {
    return String.format("#%06X", 0xFFFFFF and this.toArgb())
}