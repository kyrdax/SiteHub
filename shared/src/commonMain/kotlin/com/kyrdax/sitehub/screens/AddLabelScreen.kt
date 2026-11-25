package com.kyrdax.sitehub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.kyrdax.sitehub.extensions.hexToColor
import com.kyrdax.sitehub.extensions.toHexCode
import com.kyrdax.sitehub.models.JSONLabel
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.viewmodels.AddEditLabelViewModel
import kotlinx.coroutines.launch

@Composable
fun AddLabelScreen(
    onBackButtonPressed: () -> Unit, jsonRepository: JSONRepository
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("New label") },
                navigationIcon = {
                    IconButton(onClick = onBackButtonPressed) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        AddLabelScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            jsonRepository = jsonRepository,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun AddLabelScreenBodyContent(
    modifier: Modifier,
    jsonRepository: JSONRepository,
    snackbarHostState: SnackbarHostState
) {
    val addLabelViewModel: AddEditLabelViewModel = viewModel {
        AddEditLabelViewModel(jsonRepository = jsonRepository)
    }
    val addLabelState by addLabelViewModel.addEditLabelState.collectAsState()

    val colorController = rememberColorPickerController()
    val scope = rememberCoroutineScope()

    var labelTitleError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(320.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = addLabelState.labelTitle,
                onValueChange = {
                    addLabelViewModel.onLabelTitleChanged(it.ifBlank { "" })
                    if (addLabelState.labelTitle.isNotEmpty()) labelTitleError = false
                },
                label = { Text("Title") },
                isError = labelTitleError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            HsvColorPicker(
                modifier = Modifier.size(200.dp),
                controller = colorController,
                onColorChanged = {
                    addLabelViewModel.onSelectedColorHexChanged(it.color.toHexCode())
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = addLabelState.selectedColorHex.hexToColor(),
                            shape = CircleShape
                        )
                )
                Text(
                    text = addLabelState.selectedColorHex,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    if (addLabelState.labelTitle.isBlank()) {
                        labelTitleError = true
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Mandatory fields must be informed")
                        }
                    } else if (jsonRepository.jsonData.value.labels.any {
                            it.title.equals(addLabelState.labelTitle, ignoreCase = true)
                        }) {
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
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
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Label \"${addLabelState.labelTitle}\" created successfully!")
                        }
                        addLabelViewModel.onLabelTitleChanged("")
                        colorController.selectByColor(color = Color(0), fromUser = true)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add")
            }
        }
    }
}