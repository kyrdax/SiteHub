package com.kyrdax.sitehub

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kyrdax.sitehub.navigation.AppNavigation

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SiteHub",
    ) {
        AppNavigation()
    }
}