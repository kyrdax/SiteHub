package com.kyrdax.sitehub.states

data class AddEditLabelState(
    val labelTitle: String = "",
    val selectedColorHex: String = "",
    val labelTitleError: Boolean = false
)
