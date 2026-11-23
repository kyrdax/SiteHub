package com.kyrdax.sitehub.navigation

sealed class AppScreens(val route: String) {
    object SiteCardsScreen: AppScreens(route = "site_cards_screen")
    object AddLabelScreen: AppScreens(route = "add_label_screen")
    object DeleteLabelScreen: AppScreens(route = "delete_label_screen")
    object EditLabelScreen: AppScreens(route = "edit_label_screen")
    object AddCardScreen: AppScreens(route = "add_card_screen")
    object EditCardScreen: AppScreens(route = "edit_card_screen")
}