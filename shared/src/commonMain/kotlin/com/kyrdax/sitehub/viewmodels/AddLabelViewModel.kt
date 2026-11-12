package com.kyrdax.sitehub.viewmodels

import androidx.lifecycle.ViewModel
import com.kyrdax.sitehub.states.AddLabelState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddLabelViewModel: ViewModel() {
    private val _addLabelState = MutableStateFlow(AddLabelState())
    val addLabelState: StateFlow<AddLabelState> = _addLabelState.asStateFlow()

    fun onLabelTitleChanged(value: String) {
        _addLabelState.update { currentState ->
            currentState.copy(labelTitle = value)
        }
    }

    fun onSelectedColorHexChanged(value: String) {
        _addLabelState.update { currentState ->
            currentState.copy(selectedColorHex = value)
        }
    }

    fun onLabelTitleErrorChanged(value: Boolean) {
        _addLabelState.update { currentState ->
            currentState.copy(labelTitleError = value)
        }
    }
}