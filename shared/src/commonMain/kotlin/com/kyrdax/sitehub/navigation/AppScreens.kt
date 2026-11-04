package com.kyrdax.sitehub.navigation

sealed class AppScreens(val route: String) {
    object SiteCardsScreen: AppScreens(route = "site_cards_screen")
}