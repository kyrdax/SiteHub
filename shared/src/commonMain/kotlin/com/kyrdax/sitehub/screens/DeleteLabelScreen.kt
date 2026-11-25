package com.kyrdax.sitehub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyrdax.sitehub.extensions.hexToColor
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.viewmodels.DeleteLabelViewModel
import kotlinx.coroutines.launch

@Composable
fun DeleteLabelScreen(
    onBackButtonPressed: () -> Unit,
    onNewLabelButtonPressed: () -> Unit,
    onEditLabelButtonPressed: (labelId: Int) -> Unit,
    jsonRepository: JSONRepository
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Labels") },
                navigationIcon = {
                    IconButton(onClick = onBackButtonPressed) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewLabelButtonPressed,
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("New label") }
            )
        }
    ) { innerPadding ->
        DeleteLabelScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            jsonRepository = jsonRepository,
            snackbarHostState = snackbarHostState,
            onEditLabelButtonPressed = onEditLabelButtonPressed
        )
    }
}

@Composable
fun DeleteLabelScreenBodyContent(
    modifier: Modifier,
    jsonRepository: JSONRepository,
    snackbarHostState: SnackbarHostState,
    onEditLabelButtonPressed: (labelId: Int) -> Unit
) {
    val jsonData by jsonRepository.jsonData.collectAsState()
    val scope = rememberCoroutineScope()

    val deleteLabelViewModel: DeleteLabelViewModel = viewModel { DeleteLabelViewModel() }
    val deleteLabelState by deleteLabelViewModel.deleteLabelState.collectAsState()

    var showDeleteWarning by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = deleteLabelState.filterText,
            onValueChange = { deleteLabelViewModel.onFilterTextChanged(it.ifBlank { "" }) },
            leadingIcon = { Icon(Icons.Default.Search, "Search label") },
            placeholder = { Text("Search...") },
            singleLine = true,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 8.dp)
                .width(320.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = if (deleteLabelState.filterText.isBlank()) {
                    jsonData.labels
                } else {
                    jsonData.labels.filter {
                        it.title.contains(deleteLabelState.filterText, ignoreCase = true)
                    }
                },
                key = { it.id }
            ) { label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(label.color.hexToColor(), CircleShape)
                    )

                    Spacer(Modifier.width(16.dp))

                    Text(
                        text = label.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onEditLabelButtonPressed(label.id) }
                    )

                    IconButton(onClick = { onEditLabelButtonPressed(label.id) }) {
                        Icon(Icons.Default.Edit, "Edit label")
                    }

                    IconButton(
                        onClick = {
                            deleteLabelViewModel.onDeleteLabelIdChanged(label.id)
                            deleteLabelViewModel.onDeleteLabelNameChanged(label.title)
                            showDeleteWarning = true
                        }
                    ) {
                        Icon(Icons.Default.Delete, "Delete label")
                    }
                }
            }
        }
    }

    if (showDeleteWarning) {
        AlertDialog(
            onDismissRequest = { showDeleteWarning = false },
            title = { Text("Delete") },
            text = {
                Text("Are you sure you want to delete the label \"${deleteLabelState.deleteLabelName}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        jsonRepository.deleteLabel(id = deleteLabelState.deleteLabelId)
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Label \"${deleteLabelState.deleteLabelName}\" deleted successfully!")
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