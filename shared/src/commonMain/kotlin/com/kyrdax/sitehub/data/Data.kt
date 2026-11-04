package com.kyrdax.sitehub.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.kyrdax.sitehub.models.JSONData
import kotlinx.serialization.json.Json
import sitehub.shared.generated.resources.Res

private val json = Json {ignoreUnknownKeys = true}

suspend fun loadJSONData(): JSONData {
    val JSONString: String = Res.readBytes("files/data.json").decodeToString()
    return json.decodeFromString(JSONString)
}