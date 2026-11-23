package com.kyrdax.sitehub.viewmodels

import androidx.lifecycle.ViewModel
import com.kyrdax.sitehub.states.AddCardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddCardViewModel: ViewModel() {
    val _addCardState = MutableStateFlow(AddCardState())
    val addCardState: StateFlow<AddCardState> = _addCardState.asStateFlow()

    fun onCardTitleChanged(value: String) {
        _addCardState.update { currentState ->
            currentState.copy(cardTitle = value)
        }
    }

    fun onCardDescChanged(value: String) {
        _addCardState.update { currentState ->
            currentState.copy(cardDesc = value)
        }
    }

    fun onUrlChanged(value: String) {
        _addCardState.update { currentState ->
            currentState.copy(url = value)
        }
    }

    fun onLabelIdsChanged(value: List<Int>) {
        _addCardState.update { currentState ->
            currentState.copy(labelIds = value)
        }
    }
}