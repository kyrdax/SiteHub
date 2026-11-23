package com.kyrdax.sitehub.states

data class AddCardState (
    val cardTitle: String = "",
    val cardDesc: String = "",
    val url: String = "",
    val labelIds: List<Int> = emptyList()
)