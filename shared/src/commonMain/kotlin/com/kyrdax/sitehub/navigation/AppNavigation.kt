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
import com.kyrdax.sitehub.screens.AddCardScreen
import com.kyrdax.sitehub.screens.AddLabelScreen
import com.kyrdax.sitehub.screens.DeleteLabelScreen
import com.kyrdax.sitehub.screens.EditCardScreen
import com.kyrdax.sitehub.screens.EditLabelScreen
import com.kyrdax.sitehub.screens.SiteCardsScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val jsonRepository = JSONRepository()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SiteCardsScreen.route
    ) {
        composable(route = AppScreens.SiteCardsScreen.route) {
            SiteCardsScreen(
                onLabelsButtonPressed = {
                    navController.navigate(AppScreens.DeleteLabelScreen.route)
                },
                onNewCardButtonPressed = {
                    navController.navigate(AppScreens.AddCardScreen.route)
                },
                onEditCardButtonPressed = { cardId ->
                    navController.navigate(AppScreens.EditCardScreen.route + "/${cardId}")
                },
                jsonRepository = jsonRepository
            )
        }
        composable(route = AppScreens.AddLabelScreen.route) {
            AddLabelScreen(
                onBackButtonPressed = {
                    navController.navigate(AppScreens.DeleteLabelScreen.route)
                },
                jsonRepository = jsonRepository
            )
        }
        composable(route = AppScreens.DeleteLabelScreen.route) {
            DeleteLabelScreen(
                onBackButtonPressed = {
                    navController.navigate(AppScreens.SiteCardsScreen.route)
                },
                onNewLabelButtonPressed = {
                    navController.navigate(AppScreens.AddLabelScreen.route)
                },
                onEditLabelButtonPressed = { labelId ->
                    navController.navigate(AppScreens.EditLabelScreen.route + "/${labelId}")
                },
                jsonRepository = jsonRepository
            )
        }
        composable(
            route = AppScreens.EditLabelScreen.route + "/{id}",
            arguments = listOf(
                navArgument(name = "id") {type = NavType.IntType}
            )
        ) {
            EditLabelScreen(
                onBackButtonPressed = {
                    navController.navigate(AppScreens.DeleteLabelScreen.route)
                },
                onSaveButtonPressed = {
                    navController.navigate(AppScreens.DeleteLabelScreen.route)
                },
                labelId = it.arguments!!.read { getInt("id") },
                jsonRepository = jsonRepository
            )
        }
        composable(route = AppScreens.AddCardScreen.route) {
            AddCardScreen(
                onBackIconPressed = {
                    navController.navigate(AppScreens.SiteCardsScreen.route)
                },
                jsonRepository = jsonRepository
            )
        }
        composable(
            route = AppScreens.EditCardScreen.route + "/{id}",
            arguments = listOf(
                navArgument(name = "id") {type = NavType.IntType}
            )
        ) {
            EditCardScreen(
                onBackButtonPressed = {
                    navController.navigate(AppScreens.SiteCardsScreen.route)
                },
                onSaveButtonPressed = {
                    navController.navigate(AppScreens.SiteCardsScreen.route)
                },
                cardId = it.arguments!!.read { getInt("id") },
                jsonRepository = jsonRepository
            )
        }
    }
}