package com.kyrdax.sitehub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kyrdax.sitehub.navigation.AppScreens
import com.kyrdax.sitehub.repositories.JSONRepository

@Composable
fun SiteCardsScreen(navController: NavController, jsonRepository: JSONRepository) {
    Scaffold(
         topBar = {
             CenterAlignedTopAppBar(
                 title = {
                     Text(
                         text = "SiteHub"
                     )
                 },
                 actions = {
                     Text(
                         text = "by kyrdax"
                     )
                 }
             )
         }
    ) { innerPadding ->
        SiteCardsScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            jsonRepository = jsonRepository
        )
    }
}

@Composable
fun SiteCardsScreenBodyContent(modifier: Modifier, navController: NavController, jsonRepository: JSONRepository) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //TODO: Dropdown to filter by labels created by user

        Button(
            onClick = {
                navController.navigate(AppScreens.DeleteLabelScreen.route)
            }
        ) {
            Text(
                text = "Labels"
            )
        }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Test to know if it actually works
            items(jsonRepository.jsonData.value.labels) {
                Text(it.title)
            }
        }
    }
}