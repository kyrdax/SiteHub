package com.kyrdax.sitehub.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kyrdax.sitehub.navigation.AppScreens
import com.kyrdax.sitehub.repositories.JSONRepository
import kotlinx.coroutines.launch

@Composable
fun SiteCardsScreen(navController: NavController, jsonRepository: JSONRepository) {
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                Column {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(AppScreens.DeleteLabelScreen.route)
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Add label"
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            navController.navigate(AppScreens.AddCardScreen.route)
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add card"
                        )
                    }
                }
            }
        ) { innerPadding ->
            SiteCardsScreenBodyContent(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                jsonRepository = jsonRepository,
                snackbarHostState = snackbarHostState
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

@Composable
fun SiteCardsScreenBodyContent(modifier: Modifier, navController: NavController, jsonRepository: JSONRepository, snackbarHostState: SnackbarHostState) {
    val jsonData by jsonRepository.jsonData.collectAsState()
    val uriHandler = LocalUriHandler.current

    val scope = rememberCoroutineScope()

    var showDeleteWarning by remember { mutableStateOf(false) }
    var deleteCardId by remember { mutableStateOf(0) }
    var deleteCardName by remember { mutableStateOf("") }
    var filterText by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //TODO: Dropdown to filter by labels created by user

        OutlinedTextField(
            value = filterText,
            onValueChange = {
                filterText = it.ifBlank { "" }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search card"
                )
            },
            placeholder = {
                Text(
                    text = "Search..."
                )
            }
        )

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                if (filterText.isBlank()) jsonData.cards
                else jsonData.cards.filter { it.title.contains(other = filterText, ignoreCase = true) }
            ) { card ->
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .width(280.dp),
                    onClick = {
                        try {
                            uriHandler.openUri(card.url)
                        } catch (_: Exception) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Could not open URL \"${card.url}\"")
                            }
                        }
                    }
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
                                    navController.navigate(AppScreens.EditCardScreen.route + "/${card.id}")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit card"
                                )
                            }

                            IconButton(
                                onClick = {
                                    showDeleteWarning = true
                                    deleteCardName = card.title
                                    deleteCardId = card.id
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

    if (showDeleteWarning) {
        AlertDialog(
            onDismissRequest = {
                showDeleteWarning = false
            },
            title = {
                Text(
                    text = "Delete card"
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete the card \"$deleteCardName\"?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        jsonRepository.deleteCard(deleteCardId)
                        showDeleteWarning = false
                    }
                ) {
                    Text(
                        text = "Delete"
                    )
                }
            }
        )
    }
}