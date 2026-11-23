package com.kyrdax.sitehub.viewmodels

import androidx.lifecycle.ViewModel
import com.kyrdax.sitehub.states.DropdownMenuState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DropdownMenuViewModel(val selected: Set<String> = setOf()): ViewModel() {
    private val _dropdownMenuState = MutableStateFlow(DropdownMenuState(selected = selected))
    val dropdownMenuState: StateFlow<DropdownMenuState> = _dropdownMenuState.asStateFlow()

    fun onFilterTextChanged(value: String) {
        _dropdownMenuState.update { currentState ->
            currentState.copy(filterText = value)
        }
    }

    fun onSelectedChanged(value: Set<String>) {
        _dropdownMenuState.update { currentState ->
            currentState.copy(selected = value)
        }
    }
}