package com.kyrdax.sitehub.states

data class AddLabelState(
    val labelTitle: String = "",
    val selectedColorHex: String = "",
    val labelTitleError: Boolean = false
)
