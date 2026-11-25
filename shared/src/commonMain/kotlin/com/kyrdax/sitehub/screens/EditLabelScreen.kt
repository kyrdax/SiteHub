package com.kyrdax.sitehub.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.viewmodels.AddEditLabelViewModel
import kotlinx.coroutines.launch

@Composable
fun EditLabelScreen(
    onBackButtonPressed: () -> Unit,
    onSaveButtonPressed: () -> Unit,
    jsonRepository: JSONRepository,
    labelId: Int
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit label"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackButtonPressed()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        EditLabelScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            labelId = labelId,
            jsonRepository = jsonRepository,
            onSaveButtonPressed = onSaveButtonPressed,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun EditLabelScreenBodyContent(
    modifier: Modifier,
    jsonRepository: JSONRepository,
    labelId: Int,
    onSaveButtonPressed: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val editLabelViewModel: AddEditLabelViewModel = viewModel { AddEditLabelViewModel(jsonRepository = jsonRepository, labelId = labelId) }
        val editLabelState by editLabelViewModel.addEditLabelState.collectAsState()

        val colorController = rememberColorPickerController()
        colorController.selectByColor(
            Color(editLabelState.selectedColorHex.hexToColor().value),
            fromUser = true
        )

        val scope = rememberCoroutineScope()

        var labelTitleError by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = editLabelState.labelTitle,
            onValueChange = {
                editLabelViewModel.onLabelTitleChanged(
                    it.ifBlank { "" }
                )
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
                editLabelViewModel.onSelectedColorHexChanged(it.color.toHexCode())
            }
        )

        Canvas(Modifier.size(50.dp)) {
            drawCircle(
                color = colorController.selectedColor.value
            )
        }

        Button(
            onClick = {
                if (editLabelState.labelTitle.isBlank()) {
                    labelTitleError = true
                    editLabelViewModel.onLabelTitleChanged("")
                } else if (jsonRepository.jsonData.value.labels.filter { it.id != labelId }.any {
                        it.title.equals(
                            editLabelState.labelTitle,
                            ignoreCase = true
                        )
                    }) {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Label \"${editLabelState.labelTitle}\" already exists")
                    }
                } else {
                    jsonRepository.updateLabel(
                        id = labelId,
                        title = editLabelState.labelTitle,
                        color = editLabelState.selectedColorHex
                    )

                    onSaveButtonPressed()
                }
            }
        ) {
            Text(
                text = "Save"
            )
        }
    }
}