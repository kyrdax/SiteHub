package com.kyrdax.sitehub.viewmodels

import androidx.lifecycle.ViewModel
import com.kyrdax.sitehub.models.JSONLabel
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.states.AddEditLabelState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddEditLabelViewModel(jsonRepository: JSONRepository, labelId: Int = -1): ViewModel() {
    private val _addEditLabelState = if (labelId == -1) {
        MutableStateFlow(AddEditLabelState())
    } else {
        val jsonLabel: JSONLabel = jsonRepository.getLabelById(labelId)
        MutableStateFlow(AddEditLabelState(
            labelTitle = jsonLabel.title,
            selectedColorHex = jsonLabel.color
        ))
    }
    val addEditLabelState: StateFlow<AddEditLabelState> = _addEditLabelState.asStateFlow()

    fun onLabelTitleChanged(value: String) {
        _addEditLabelState.update { currentState ->
            currentState.copy(labelTitle = value)
        }
    }

    fun onSelectedColorHexChanged(value: String) {
        _addEditLabelState.update { currentState ->
            currentState.copy(selectedColorHex = value)
        }
    }

    fun onLabelTitleErrorChanged(value: Boolean) {
        _addEditLabelState.update { currentState ->
            currentState.copy(labelTitleError = value)
        }
    }
}