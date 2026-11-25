package com.kyrdax.sitehub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyrdax.sitehub.components.CustomDropdownMenu
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.viewmodels.SiteCardsViewModel
import kotlinx.coroutines.launch

@Composable
fun SiteCardsScreen(
    onLabelsButtonPressed: () -> Unit,
    onNewCardButtonPressed: () -> Unit,
    onEditCardButtonPressed: (cardId: Int) -> Unit,
    jsonRepository: JSONRepository
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SiteHub") },
                actions = {
                    Text(
                        text = "by kyrdax",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = onLabelsButtonPressed,
                    icon = { Icon(Icons.Default.Menu, "Labels") },
                    text = { Text("Labels") }
                )
                ExtendedFloatingActionButton(
                    onClick = onNewCardButtonPressed,
                    icon = { Icon(Icons.Default.Add, "New card") },
                    text = { Text("New card") }
                )
            }
        }
    ) { innerPadding ->
        SiteCardsScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            jsonRepository = jsonRepository,
            snackbarHostState = snackbarHostState,
            onEditCardButtonPressed = onEditCardButtonPressed
        )
    }
}

@Composable
fun SiteCardsScreenBodyContent(
    modifier: Modifier,
    jsonRepository: JSONRepository,
    snackbarHostState: SnackbarHostState,
    onEditCardButtonPressed: (cardId: Int) -> Unit
) {
    val jsonData by jsonRepository.jsonData.collectAsState()
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    var showDeleteWarning by remember { mutableStateOf(false) }

    val siteCardsViewModel = viewModel { SiteCardsViewModel() }
    val siteCardsState by siteCardsViewModel.siteCardsState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = siteCardsState.filterText,
                onValueChange = { siteCardsViewModel.onFilterTextChanged(it.ifBlank { "" }) },
                leadingIcon = { Icon(Icons.Default.Search, "Search card") },
                placeholder = { Text("Search...") },
                singleLine = true,
                modifier = Modifier.width(320.dp)
            )

            CustomDropdownMenu(
                jsonRepository = jsonRepository,
                cardState = siteCardsState,
                cardViewModel = siteCardsViewModel,
                modifier = Modifier.width(320.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                when {
                    siteCardsState.filterText.isNotBlank() && siteCardsState.labelIds.isNotEmpty() -> {
                        jsonData.cards.filter {
                            it.title.contains(siteCardsState.filterText, ignoreCase = true) &&
                                    it.labels.containsAll(siteCardsState.labelIds)
                        }
                    }
                    siteCardsState.filterText.isNotBlank() -> {
                        jsonData.cards.filter {
                            it.title.contains(siteCardsState.filterText, ignoreCase = true)
                        }
                    }
                    siteCardsState.labelIds.isNotEmpty() -> {
                        jsonData.cards.filter {
                            it.labels.containsAll(siteCardsState.labelIds)
                        }
                    }
                    else -> jsonData.cards
                }
            ) { card ->
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.width(320.dp),
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = card.title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (card.description.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = card.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { onEditCardButtonPressed(card.id) }) {
                                Icon(Icons.Default.Edit, "Edit card")
                            }
                            IconButton(
                                onClick = {
                                    showDeleteWarning = true
                                    siteCardsViewModel.onDeleteCardNameChanged(card.title)
                                    siteCardsViewModel.onDeleteCardIdChanged(card.id)
                                }
                            ) {
                                Icon(Icons.Default.Delete, "Delete card")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteWarning) {
        AlertDialog(
            onDismissRequest = { showDeleteWarning = false },
            title = { Text("Delete card") },
            text = {
                Text("Are you sure you want to delete the card \"${siteCardsState.deleteCardName}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        jsonRepository.deleteCard(siteCardsState.deleteCardId)
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Card \"${siteCardsState.deleteCardName}\" deleted successfully!")
                        }
                        showDeleteWarning = false
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWarning = false }) { Text("Cancel") }
            }
        )
    }
}