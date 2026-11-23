package com.kyrdax.sitehub.states

data class DropdownMenuState(
    val filterText: String = "",
    val selected: Set<String> = setOf(),
)
