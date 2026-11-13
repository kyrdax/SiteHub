package com.kyrdax.sitehub.viewmodels

import androidx.lifecycle.ViewModel
import com.kyrdax.sitehub.states.DeleteLabelState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeleteLabelViewModel: ViewModel() {
    private val _deleteLabelState = MutableStateFlow(DeleteLabelState())
    val deleteLabelState: StateFlow<DeleteLabelState> = _deleteLabelState.asStateFlow()

    fun onShowDeleteWarningChanged(value: Boolean) {
        _deleteLabelState.update { currentState ->
            currentState.copy(showDeleteWarning = value)
        }
    }

    fun onDeleteLabelNameChanged(value: String) {
        _deleteLabelState.update { currentState ->
            currentState.copy(deleteLabelName = value)
        }
    }

    fun onFilterTextChanged(value: String) {
        _deleteLabelState.update { currentState ->
            currentState.copy(filterText = value)
        }
    }
}