package com.kyrdax.sitehub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.screens.AddLabelScreen
import com.kyrdax.sitehub.screens.DeleteLabelScreen
import com.kyrdax.sitehub.screens.EditLabelScreen
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
        composable(route = AppScreens.DeleteLabelScreen.route) {
            DeleteLabelScreen(navController, jsonRepository)
        }
        composable(
            route = AppScreens.EditLabelScreen.route + "/{id}",
            arguments = listOf(
                navArgument(name = "id") {type = NavType.IntType}
            )
        ) {
            EditLabelScreen(
                navController = navController,
                labelId = it.arguments!!.read { getInt("id") },
                jsonRepository = jsonRepository
            )
        }
    }
}