package com.kyrdax.sitehub.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kyrdax.sitehub.navigation.AppScreens
import com.kyrdax.sitehub.repositories.JSONRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyrdax.sitehub.components.CustomDropdownMenu
import com.kyrdax.sitehub.extensions.hexToColor
import com.kyrdax.sitehub.models.JSONCard
import com.kyrdax.sitehub.states.AddCardState
import com.kyrdax.sitehub.viewmodels.AddCardViewModel

@Composable
fun AddCardScreen(navController: NavController, jsonRepository: JSONRepository) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add card"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(AppScreens.SiteCardsScreen.route)
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
        AddCardScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            snackbarHostState = snackbarHostState,
            jsonRepository = jsonRepository
        )
    }
}

@Composable
fun AddCardScreenBodyContent(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    jsonRepository: JSONRepository
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val addCardViewModel: AddCardViewModel = viewModel { AddCardViewModel() }
        val addCardState by addCardViewModel.addCardState.collectAsState()

        var titleError by remember { mutableStateOf(false) }
        var urlError by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        OutlinedTextField(
            value = addCardState.cardTitle,
            onValueChange = {
                titleError = false
                addCardViewModel.onCardTitleChanged(it)
            },
            label = {
                Text(
                    text = "Title"
                )
            },
            isError = titleError
        )

        OutlinedTextField(
            value = addCardState.url,
            onValueChange = {
                urlError = false
                addCardViewModel.onUrlChanged(it)
            },
            label = {
                Text(
                    text = "URL"
                )
            },
            isError = urlError
        )

        OutlinedTextField(
            value = addCardState.cardDesc,
            onValueChange = {
                addCardViewModel.onCardDescChanged(it)
            },
            label = {
                Text(
                    text = "Description (optional)"
                )
            }
        )

        CustomDropdownMenu(
            jsonRepository = jsonRepository,
            addCardViewModel = addCardViewModel,
            addCardState = addCardState
        )

        Button(
            onClick = {
                if (addCardState.url.isBlank() || addCardState.cardTitle.isBlank()) {
                    if (addCardState.cardTitle.isBlank()) {
                        titleError = true
                        addCardViewModel.onCardTitleChanged("")
                    }

                    if (addCardState.url.isBlank()) {
                        urlError = true
                        addCardViewModel.onUrlChanged("")
                    }

                    scope.launch {
                        snackbarHostState.showSnackbar("Mandatory fields must be informed")
                    }
                } else {
                    jsonRepository.add(JSONCard(
                        title = addCardState.cardTitle,
                        description = addCardState.cardDesc,
                        url = addCardState.url,
                        labels = addCardState.labelIds
                    ))
                }
            }
        ) {
            Text(
                text = "Add"
            )
        }
    }
}