package com.kyrdax.sitehub.models

import kotlinx.serialization.Serializable

@Serializable
data class JSONLabel(
    val id: Int = 0,
    val title: String,
    val color: String
)

@Serializable
data class JSONCard(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val url: String,
    val labels: List<Int> = emptyList()
)

@Serializable
data class JSONData(
    val labels: List<JSONLabel> = emptyList(),
    val cards: List<JSONCard> = emptyList()
)