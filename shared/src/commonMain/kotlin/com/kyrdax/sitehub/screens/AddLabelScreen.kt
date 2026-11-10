package com.kyrdax.sitehub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.kyrdax.sitehub.extensions.toHexCode
import com.kyrdax.sitehub.models.JSONLabel
import com.kyrdax.sitehub.navigation.AppScreens
import com.kyrdax.sitehub.repositories.JSONRepository
import kotlinx.coroutines.launch

@Composable
fun AddLabelScreen(navController: NavController, jsonRepository: JSONRepository) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New label"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick =  {
                            navController.navigate(AppScreens.EditDeleteLabelScreen.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AddLabelScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            jsonRepository = jsonRepository,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun AddLabelScreenBodyContent(modifier: Modifier, navController: NavController, jsonRepository: JSONRepository, snackbarHostState: SnackbarHostState) {
    var labelTitle by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf("") }
    var labelTitleError by remember { mutableStateOf(false) }

    val colorController = rememberColorPickerController()
    val scope = rememberCoroutineScope()

    Column(
        modifier  = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = labelTitle,
            onValueChange = {
                labelTitle = it

                if(labelTitle.isNotEmpty()) {
                    labelTitleError = false
                }
            },
            label = {
                Text(
                    text = "Title"
                )
            },
            isError = labelTitleError
        )

        HsvColorPicker(
            modifier = Modifier.width(200.dp)
                .height(200.dp),
            controller = colorController,
            onColorChanged = {
                selectedColorHex = it.color.toHexCode()
            }
        )

        Button(
            onClick = {
                if (labelTitle.isBlank()) {
                    labelTitleError = true
                } else if (jsonRepository.jsonData.value.labels.filter { it.title == labelTitle }.isNotEmpty()) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Label \"$labelTitle\" already exists")
                    }
                } else {
                    jsonRepository.add(
                        JSONLabel(
                            title = labelTitle,
                            color = selectedColorHex
                        )
                    )

                    scope.launch {
                        snackbarHostState.showSnackbar("Label \"$labelTitle\" created successfully!")
                    }

                    scope.launch {
                        labelTitle = ""
                    }
                }
            }
        ) {
            Text(
                text = "Add"
            )
        }

    }
}