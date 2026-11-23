package com.kyrdax.sitehub.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyrdax.sitehub.extensions.hexToColor
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.states.AddEditCardState
import com.kyrdax.sitehub.viewmodels.AddEditCardViewModel
import com.kyrdax.sitehub.viewmodels.DropdownMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    jsonRepository: JSONRepository,
    cardViewModel: AddEditCardViewModel,
    cardState: AddEditCardState
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    val dropdownMenuViewModel = viewModel { DropdownMenuViewModel(selected =
        cardState.labelIds.map { jsonRepository.getLabelById(it).title }.toSet()
    ) }
    val dropdownMenuState by dropdownMenuViewModel.dropdownMenuState.collectAsState()

    val jsonData by jsonRepository.jsonData.collectAsState()
    val filtered = jsonData.labels.filter { it.title.contains(dropdownMenuState.filterText, ignoreCase = true) }
    val singleSelectedLabel = if (dropdownMenuState.selected.size == 1) {
        jsonData.labels.firstOrNull { it.title == dropdownMenuState.selected.first() }
    } else null

    Column() {
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = {
                dropdownExpanded = it
                if (!dropdownExpanded) dropdownMenuViewModel.onFilterTextChanged("")
            }
        ) {
            OutlinedTextField(
                value = if (dropdownExpanded) {
                    dropdownMenuState.filterText
                } else {
                    when (dropdownMenuState.selected.size) {
                        0 -> ""
                        1 -> dropdownMenuState.selected.first()
                        else -> "${dropdownMenuState.selected.size} elements selected"
                    }
                },
                onValueChange = {
                    dropdownMenuViewModel.onFilterTextChanged(it)
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
                    dropdownMenuViewModel.onFilterTextChanged("")
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
                        val checked = label.title in dropdownMenuState.selected
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
                                dropdownMenuViewModel.onSelectedChanged(if (checked) dropdownMenuState.selected - label.title else dropdownMenuState.selected + label.title)
                                cardViewModel.onLabelIdsChanged(if (checked) cardState.labelIds - label.id else cardState.labelIds + label.id)
                            }
                        )
                    }
                }
            }
        }
    }
}