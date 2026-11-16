package com.kyrdax.sitehub

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kyrdax.sitehub.navigation.AppNavigation

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        resizable = false,
        title = "SiteHub"
    ) {
        AppNavigation()
    }
}