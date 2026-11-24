package com.kyrdax.sitehub.states

data class SiteCardsState(
    val deleteCardId: Int = 0,
    var deleteCardName: String = "",
    var filterText: String = "",
    var labelIds: List<Int> = emptyList()
)
