package com.kyrdax.sitehub.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kyrdax.sitehub.models.JSONData
import com.kyrdax.sitehub.navigation.AppScreens
import com.kyrdax.sitehub.repositories.JSONRepository

@Composable
fun AddLabelScreen(navController: NavController, jsonRepository: JSONRepository) {
    Scaffold() { innerPadding ->
        AddLabelScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            jsonRepository = jsonRepository
        )
    }
}

@Composable
fun AddLabelScreenBodyContent(modifier: Modifier, navController: NavController, jsonRepository: JSONRepository) {
    var labelTitle by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    Column(
        modifier  = modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = labelTitle,
            onValueChange = {
                labelTitle = it
            },
            label = {
                Text(
                    text = "Title"
                )
            }
        )

        OutlinedTextField(
            value = url,
            onValueChange = {
                url = it
            },
            label = {
                Text(
                    text = "URL"
                )
            }
        )

        Row(

        ) {
            Button(
                onClick = {
                    navController.navigate(AppScreens.SiteCardsScreen.route)
                }
            ) {
                Text(
                    text = "Cancel"
                )
            }

            Button(
                onClick = {
                    //jsonRepository.add()
                }
            ) {
                Text(
                    text = "Add"
                )
            }
        }


    }
}