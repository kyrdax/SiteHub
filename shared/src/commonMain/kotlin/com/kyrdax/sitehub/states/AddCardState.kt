package com.kyrdax.sitehub.states

data class AddCardState (
    val cardTitle: String = "",
    val cardDesc: String = "",
    val url: String = "",
    val filterText: String = "",
    val selected: Set<String> = setOf()
)