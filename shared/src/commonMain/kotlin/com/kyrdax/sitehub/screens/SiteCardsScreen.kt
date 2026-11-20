package com.kyrdax.sitehub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
         },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AppScreens.AddCardScreen.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add card"
                )
            }
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
    val jsonData by jsonRepository.jsonData.collectAsState()

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
            items(jsonData.cards) { card ->
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .width(280.dp)
                ) {
                    Text(
                        text = card.title,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = card.description,
                            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                            textAlign = TextAlign.Center
                        )

                        Column {
                            IconButton(
                                onClick = {
                                    // TODO: Edit
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit card"
                                )
                            }

                            IconButton(
                                onClick = {
                                    // TODO: Delete
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete card"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}