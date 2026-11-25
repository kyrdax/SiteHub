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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit label") },
                navigationIcon = {
                    IconButton(onClick = onBackButtonPressed) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                }
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
    val editLabelViewModel: AddEditLabelViewModel = viewModel {
        AddEditLabelViewModel(jsonRepository = jsonRepository, labelId = labelId)
    }
    val editLabelState by editLabelViewModel.addEditLabelState.collectAsState()

    val colorController = rememberColorPickerController()
    colorController.selectByColor(
        Color(editLabelState.selectedColorHex.hexToColor().value),
        fromUser = true
    )

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
                value = editLabelState.labelTitle,
                onValueChange = {
                    labelTitleError = false
                    editLabelViewModel.onLabelTitleChanged(it.ifBlank { "" })
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
                    editLabelViewModel.onSelectedColorHexChanged(it.color.toHexCode())
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
                            color = editLabelState.selectedColorHex.hexToColor(),
                            shape = CircleShape
                        )
                )
                Text(
                    text = editLabelState.selectedColorHex,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    if (editLabelState.labelTitle.isBlank()) {
                        labelTitleError = true
                        editLabelViewModel.onLabelTitleChanged("")
                    } else if (jsonRepository.jsonData.value.labels.filter { it.id != labelId }.any {
                            it.title.equals(editLabelState.labelTitle, ignoreCase = true)
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
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}