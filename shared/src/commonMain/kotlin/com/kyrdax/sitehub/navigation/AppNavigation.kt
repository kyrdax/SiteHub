package com.kyrdax.sitehub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(): Unit {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SiteCardsScreen.route
    ) {
        composable(route = AppScreens.SiteCardsScreen.route) {  }
    }
}