package com.kyrdax.sitehub

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kyrdax.sitehub.navigation.AppNavigation
import com.kyrdax.sitehub.shared.resources.Res
import com.kyrdax.sitehub.shared.resources.sitehub_logo
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        resizable = false,
        title = "SiteHub",
        icon = painterResource(Res.drawable.sitehub_logo)
    ) {
        AppNavigation()
    }
}