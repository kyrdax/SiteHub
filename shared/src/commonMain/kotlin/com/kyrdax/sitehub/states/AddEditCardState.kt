package com.kyrdax.sitehub.states

data class AddEditCardState (
    val cardTitle: String = "",
    val cardDesc: String = "",
    val url: String = "",
    val labelIds: List<Int> = emptyList()
)