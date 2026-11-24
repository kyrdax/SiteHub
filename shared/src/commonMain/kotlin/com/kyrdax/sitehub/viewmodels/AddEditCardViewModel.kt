package com.kyrdax.sitehub.viewmodels

import androidx.lifecycle.ViewModel
import com.kyrdax.sitehub.repositories.JSONRepository
import com.kyrdax.sitehub.states.AddEditCardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddEditCardViewModel(jsonRepository: JSONRepository, cardId: Int = -1): ViewModel() {
    private val _addCardState = if (cardId == -1) {
        MutableStateFlow(AddEditCardState())
    } else {
        var jsonCard = jsonRepository.getCardById(cardId)

        MutableStateFlow(AddEditCardState(
            cardTitle = jsonCard.title,
            cardDesc = jsonCard.description,
            url = jsonCard.url,
            labelIds = jsonCard.labels
        ))
    }
    val addCardState: StateFlow<AddEditCardState> = _addCardState.asStateFlow()

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

    fun onClearLabelsChanged(value: Boolean) {
        _addCardState.update { currentState ->
            currentState.copy(clearLabels = value)
        }
    }
}