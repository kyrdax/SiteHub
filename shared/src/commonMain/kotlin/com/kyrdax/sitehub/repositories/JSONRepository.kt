package com.kyrdax.sitehub.repositories

import com.kyrdax.sitehub.models.JSONCard
import com.kyrdax.sitehub.models.JSONData
import com.kyrdax.sitehub.models.JSONLabel
import dataDirectory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath

class JSONRepository {
    private val path = "${dataDirectory()}/data.json".toPath()
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    private val _jsonData = MutableStateFlow(load())
    val jsonData: StateFlow<JSONData> = _jsonData.asStateFlow()

    fun add(jsonLabel: JSONLabel) {
        _jsonData.update { currentState ->
            currentState.copy(labels = currentState.labels + JSONLabel(
                id = getNextFreeLabelId(),
                title = jsonLabel.title,
                color = jsonLabel.color
            ))
        }

        save(_jsonData.value)
    }

    fun add(jsonCard: JSONCard) {
        _jsonData.update { currentState ->
            currentState.copy(cards = currentState.cards + jsonCard)
        }

        save(_jsonData.value)
    }

    fun deleteLabel(id: Int) {
        _jsonData.update { currentState ->
            currentState.copy(
                labels = currentState.labels.filterNot { it.id == id },
                cards = currentState.cards.map { card ->
                    card.copy(labels = card.labels - id)
                }
            )
        }

        save(_jsonData.value)
    }

    fun updateLabel(id: Int, title: String, color: String) {
        _jsonData.update { currentState ->
            currentState.copy(
                labels = currentState.labels.map { label ->
                    if (label.id == id) label.copy(title = title, color = color) else label
                }
            )
        }

        save(_jsonData.value)
    }

    fun load(): JSONData {
        if (!FileSystem.SYSTEM.exists(path)) return JSONData()
        return try {
            val jsonText: String = FileSystem.SYSTEM.read(path) {readUtf8()}
            json.decodeFromString(jsonText)
        } catch (_: SerializationException) {
            JSONData()
        }
    }

    fun save(jsonData: JSONData): Unit {
        FileSystem.SYSTEM.write(path) {
            writeUtf8(json.encodeToString(jsonData))
        }
    }

    private fun getNextFreeLabelId(): Int {
        return if (jsonData.value.labels.isEmpty()) 1 else jsonData.value.labels.last().id + 1
    }
}