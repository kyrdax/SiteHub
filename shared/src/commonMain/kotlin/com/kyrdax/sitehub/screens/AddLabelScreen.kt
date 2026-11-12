package com.kyrdax.sitehub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.kyrdax.sitehub.extensions.toHexCode
import com.kyrdax.sitehub.models.JSONLabel
import com.kyrdax.sitehub.navigation.AppScreens
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.viewmodels.AddLabelViewModel
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
                            navController.navigate(AppScreens.DeleteLabelScreen.route)
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
    val addLabelViewModel: AddLabelViewModel = viewModel()
    val addLabelState by addLabelViewModel.addLabelState.collectAsState()

    val colorController = rememberColorPickerController()
    val scope = rememberCoroutineScope()

    Column(
        modifier  = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = addLabelState.labelTitle,
            onValueChange = {
                addLabelViewModel.onLabelTitleChanged(it)

                if(addLabelState.labelTitle.isNotEmpty()) {
                    addLabelViewModel.onLabelTitleErrorChanged(false)
                }
            },
            label = {
                Text(
                    text = "Title"
                )
            },
            isError = addLabelState.labelTitleError
        )

        HsvColorPicker(
            modifier = Modifier.width(200.dp)
                .height(200.dp),
            controller = colorController,
            onColorChanged = {
                addLabelViewModel.onSelectedColorHexChanged(it.color.toHexCode())
            }
        )

        Button(
            onClick = {
                if (addLabelState.labelTitle.isBlank()) {
                    addLabelViewModel.onLabelTitleErrorChanged(true)
                } else if (jsonRepository.jsonData.value.labels.filter { it.title == addLabelState.labelTitle }.isNotEmpty()) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Label \"${addLabelState.labelTitle}\" already exists")
                    }
                } else {
                    jsonRepository.add(
                        JSONLabel(
                            title = addLabelState.labelTitle,
                            color = addLabelState.selectedColorHex
                        )
                    )

                    scope.launch {
                        snackbarHostState.showSnackbar("Label \"${addLabelState.labelTitle}\" created successfully!")
                    }

                    scope.launch {
                        addLabelViewModel.onLabelTitleChanged("")
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