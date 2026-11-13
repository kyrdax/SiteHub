package com.kyrdax.sitehub.states

data class DeleteLabelState (
    val showDeleteWarning: Boolean = false,
    val deleteLabelName: String = "",
    val filterText: String = ""
)