package com.kyrdax.sitehub.states

data class DeleteLabelState (
    val deleteLabelId: Int = 0,
    val deleteLabelName: String = "",
    val filterText: String = ""
)