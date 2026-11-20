package com.kyrdax.sitehub.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentRecomposeScope
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
import androidx.navigation.NavController
import com.kyrdax.sitehub.extensions.hexToColor
import com.kyrdax.sitehub.navigation.AppScreens
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.viewmodels.DeleteLabelViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource

@Composable
fun DeleteLabelScreen(navController: NavController, jsonRepository: JSONRepository) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Labels"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick =  {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AppScreens.AddLabelScreen.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        DeleteLabelScreenBodyContent(
            modifier = Modifier.padding(innerPadding),
            jsonRepository = jsonRepository,
            snackbarHostState = snackbarHostState,
            navController
        )
    }
}

@Composable
fun DeleteLabelScreenBodyContent(
    modifier: Modifier,
    jsonRepository: JSONRepository,
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {
    val jsonData by jsonRepository.jsonData.collectAsState()
    val scope = rememberCoroutineScope()

    val deleteLabelViewModel: DeleteLabelViewModel = viewModel { DeleteLabelViewModel() }
    val deleteLabelState by deleteLabelViewModel.deleteLabelState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
            .padding(0.dp, 0.dp, 0.dp, 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = deleteLabelState.filterText,
            onValueChange = {
                deleteLabelViewModel.onFilterTextChanged(it)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search label"
                )
            }
        )

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = if (deleteLabelState.filterText.isBlank()) {
                    jsonData.labels
                } else {
                    jsonData.labels.filter {
                        it.title.contains(
                            other = deleteLabelState.filterText,
                            ignoreCase = true
                        )
                    }
                },
                key = {it.id}
            ) { label ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(Modifier.size(24.dp)) {
                        drawCircle(
                            color = label.color.hexToColor()
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Box{
                        OutlinedTextField(
                            value = label.title,
                            readOnly = true,
                            onValueChange = {},
                            modifier = Modifier.width(200.dp)
                        )

                        Box(
                            Modifier.matchParentSize().clickable(
                                enabled = true,
                                onClick = {
                                    navController.navigate(AppScreens.EditLabelScreen.route + "/${label.id}")
                                }
                            )
                        )
                    }

                    IconButton(
                        onClick = {
                            navController.navigate(AppScreens.EditLabelScreen.route + "/${label.id}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit label"
                        )
                    }

                    IconButton(
                        onClick = {
                            deleteLabelViewModel.onDeleteLabelNameChanged(label.title)
                            deleteLabelViewModel.onShowDeleteWarningChanged(true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete label"
                        )
                    }
                }
            }
        }
    }

    if(deleteLabelState.showDeleteWarning) {
        AlertDialog(
            onDismissRequest = {
                deleteLabelViewModel.onShowDeleteWarningChanged(false)
            },
            title = {
                Text(
                    text = "Delete"
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete the label \"${deleteLabelState.deleteLabelName}\"?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        jsonRepository.deleteLabel(
                            id = jsonData.labels.first{ it.title == deleteLabelState.deleteLabelName }.id
                        )

                        scope.launch {
                            snackbarHostState.showSnackbar("Label \"${deleteLabelState.deleteLabelName}\" deleted successfully!")
                        }

                        deleteLabelViewModel.onShowDeleteWarningChanged(false)
                    }
                ) {
                    Text(
                        text = "Delete"
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deleteLabelViewModel.onShowDeleteWarningChanged(false)
                    }
                ) {
                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}