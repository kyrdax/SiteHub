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
import com.kyrdax.sitehub.extensions.hexToColor

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
        var cardTitle by remember { mutableStateOf("") }
        var cardDesc by remember { mutableStateOf("") }
        var url by remember { mutableStateOf("") }

        var titleError by remember { mutableStateOf(false) }
        var urlError by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        OutlinedTextField(
            value = cardTitle,
            onValueChange = {
                titleError = false
                cardTitle = it
            },
            label = {
                Text(
                    text = "Title"
                )
            },
            isError = titleError
        )

        OutlinedTextField(
            value = url,
            onValueChange = {
                urlError = false
                url = it
            },
            label = {
                Text(
                    text = "URL"
                )
            },
            isError = urlError
        )

        OutlinedTextField(
            value = cardDesc,
            onValueChange = {
                cardDesc = it
            },
            label = {
                Text(
                    text = "Description (optional)"
                )
            }
        )

        CustomDropdownMenu(jsonRepository)

        Button(
            onClick = {
                if (url.isBlank() || cardTitle.isBlank()) {
                    if (cardTitle.isBlank()) {
                        titleError = true
                    }

                    if (url.isBlank()) {
                        urlError = true
                    }

                    scope.launch {
                        snackbarHostState.showSnackbar("Mandatory fields must be informed")
                    }
                } else {
                    //TODO: Implement card addition
                }
            }
        ) {
            Text(
                text = "Add"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(jsonRepository: JSONRepository) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf(setOf<String>()) }

    val jsonData by jsonRepository.jsonData.collectAsState()
    val filtered = jsonData.labels.filter { it.title.contains(filterText, ignoreCase = true) }
    val singleSelectedLabel = if (selected.size == 1) {
        jsonData.labels.firstOrNull { it.title == selected.first() }
    } else null

    Column() {
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = {
                dropdownExpanded = it
                if (!dropdownExpanded) filterText = ""
            }
        ) {
            OutlinedTextField(
                value = if (dropdownExpanded) {
                    filterText
                } else {
                    when (selected.size) {
                        0 -> ""
                        1 -> selected.first()
                        else -> "${selected.size} elements selected"
                    }
                },
                onValueChange = {
                    filterText = it
                    dropdownExpanded = true
                },
                label = {
                    Text(
                        text = "Labels (optional)"
                    )
                },
                placeholder = {
                    Text(
                        text = "Search..."
                    )
                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                leadingIcon = {
                    if (!dropdownExpanded && singleSelectedLabel != null) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            drawCircle(color = singleSelectedLabel.color.hexToColor())
                        }
                    }
                }
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = {
                    dropdownExpanded = false
                    filterText = ""
                }
            ) {
                if (filtered.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "No results"
                            )
                        },
                        onClick = {},
                        enabled = false
                    )
                } else {
                    filtered.forEach { label ->
                        val checked = label.title in selected
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = label.title
                                )
                            },
                            leadingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = null
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        drawCircle(color = label.color.hexToColor())
                                    }
                                }
                            },
                            onClick = {
                                selected = if (checked) selected - label.title else selected + label.title
                            }
                        )
                    }
                }
            }
        }
    }
}