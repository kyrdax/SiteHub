package com.kyrdax.sitehub.extensions

import androidx.compose.ui.graphics.Color

fun String.hexToColor(): Color {
    val hex = removePrefix("#")
    val argb = when (hex.length) {
        6 -> 0xFF000000 or hex.toLong(16)
        8 -> hex.toLong(16)
        else -> throw IllegalArgumentException("Invalid Hex: $this")
    }
    return Color(argb.toInt())
}