package com.kyrdax.sitehub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.screens.AddLabelScreen
import com.kyrdax.sitehub.screens.DeleteLabelScreen
import com.kyrdax.sitehub.screens.SiteCardsScreen

@Composable
fun AppNavigation(): Unit {
    val navController: NavHostController = rememberNavController()
    val jsonRepository: JSONRepository = JSONRepository()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SiteCardsScreen.route
    ) {
        composable(route = AppScreens.SiteCardsScreen.route) {
            SiteCardsScreen(
                navController = navController,
                jsonRepository = jsonRepository
            )
        }
        composable(route = AppScreens.AddLabelScreen.route) {
            AddLabelScreen(navController, jsonRepository)
        }
        composable(route = AppScreens.EditDeleteLabelScreen.route) {
            DeleteLabelScreen(navController, jsonRepository)
        }
    }
}