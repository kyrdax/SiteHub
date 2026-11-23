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
import com.kyrdax.sitehub.extensions.hexToColor
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.states.AddCardState
import com.kyrdax.sitehub.viewmodels.AddCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    jsonRepository: JSONRepository,
    addCardViewModel: AddCardViewModel,
    addCardState: AddCardState
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    val jsonData by jsonRepository.jsonData.collectAsState()
    val filtered = jsonData.labels.filter { it.title.contains(addCardState.filterText, ignoreCase = true) }
    val singleSelectedLabel = if (addCardState.selected.size == 1) {
        jsonData.labels.firstOrNull { it.title == addCardState.selected.first() }
    } else null

    Column() {
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = {
                dropdownExpanded = it
                if (!dropdownExpanded) addCardViewModel.onFilterTextChanged("")
            }
        ) {
            OutlinedTextField(
                value = if (dropdownExpanded) {
                    addCardState.filterText
                } else {
                    when (addCardState.selected.size) {
                        0 -> ""
                        1 -> addCardState.selected.first()
                        else -> "${addCardState.selected.size} elements selected"
                    }
                },
                onValueChange = {
                    addCardViewModel.onFilterTextChanged(it)
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
                    addCardViewModel.onFilterTextChanged("")
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
                        val checked = label.title in addCardState.selected
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
                                addCardViewModel.onSelectedChanged(if (checked) addCardState.selected - label.title else addCardState.selected + label.title)
                                addCardViewModel.onLabelIdsChanged(if (checked) addCardState.labelIds - label.id else addCardState.labelIds + label.id)
                            }
                        )
                    }
                }
            }
        }
    }
}