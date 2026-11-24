package com.kyrdax.sitehub.viewmodels

import androidx.lifecycle.ViewModel
import com.kyrdax.sitehub.states.SiteCardsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SiteCardsViewModel: ViewModel() {
    private val _siteCardsState = MutableStateFlow(SiteCardsState())
    val siteCardsState: StateFlow<SiteCardsState> = _siteCardsState.asStateFlow()

    fun onDeleteCardIdChanged(value: Int) {
        _siteCardsState.update { currentState ->
            currentState.copy(deleteCardId = value)
        }
    }

    fun onDeleteCardNameChanged(value: String) {
        _siteCardsState.update { currentState ->
            currentState.copy(deleteCardName = value)
        }
    }

    fun onFilterTextChanged(value: String) {
        _siteCardsState.update { currentState ->
            currentState.copy(filterText = value)
        }
    }

    fun onLabelIdsChanged(value: List<Int>) {
        _siteCardsState.update { currentState ->
            currentState.copy(labelIds = value)
        }
    }
}