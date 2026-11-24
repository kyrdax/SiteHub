package com.kyrdax.sitehub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyrdax.sitehub.components.CustomDropdownMenu
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.viewmodels.AddEditCardViewModel
import kotlinx.coroutines.launch

@Composable
fun EditCardScreen(
    onBackButtonPressed: () -> Unit,
    onSaveButtonPressed: () -> Unit,
    cardId: Int,
    jsonRepository: JSONRepository,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
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
                },
                title = {
                    Text(
                        text = "Edit card"
                    )
                }
            )
        }
    ) { innerPadding ->
        EditCardScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            jsonRepository = jsonRepository,
            snackbarHostState = snackbarHostState,
            cardId = cardId,
            onSaveButtonPressed = onSaveButtonPressed
        )
    }
}

@Composable
fun EditCardScreenBodyContent(
    modifier: Modifier,
    jsonRepository: JSONRepository,
    snackbarHostState: SnackbarHostState,
    cardId: Int,
    onSaveButtonPressed: () -> Unit
) {
    var titleError by remember { mutableStateOf(false) }
    var urlError by remember { mutableStateOf(false) }

    val editCardViewModel = viewModel { AddEditCardViewModel(jsonRepository = jsonRepository, cardId = cardId) }
    val editCardState by editCardViewModel.addCardState.collectAsState()

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = editCardState.cardTitle,
            onValueChange = {
                editCardViewModel.onCardTitleChanged(it.ifBlank { "" })
            },
            label = {
                Text(
                    text = "Title"
                )
            }
        )

        OutlinedTextField(
            value = editCardState.url,
            onValueChange = {
                editCardViewModel.onUrlChanged(it.ifBlank { "" })
            },
            label = {
                Text(
                    text = "URL"
                )
            }
        )

        OutlinedTextField(
            value = editCardState.cardDesc,
            onValueChange = {
                editCardViewModel.onCardDescChanged(it.ifBlank { "" })
            },
            label = {
                Text(
                    text = "Description (optional)"
                )
            }
        )

        CustomDropdownMenu(
            jsonRepository = jsonRepository,
            cardViewModel = editCardViewModel,
            cardState = editCardState
        )

        Button(
            onClick = {
                if (editCardState.url.isBlank() || editCardState.cardTitle.isBlank()) {
                    if (editCardState.cardTitle.isBlank()) {
                        titleError = true
                    }

                    if (editCardState.url.isBlank()) {
                        urlError = true
                    }

                    scope.launch {
                        snackbarHostState.showSnackbar("Mandatory fields must be informed")
                    }
                } else {
                    jsonRepository.updateCard(
                        id = cardId,
                        title = editCardState.cardTitle,
                        description = editCardState.cardDesc,
                        url = editCardState.url,
                        labels = editCardState.labelIds
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