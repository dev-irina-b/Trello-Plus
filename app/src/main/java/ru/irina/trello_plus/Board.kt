package ru.irina.trello_plus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Board (
    val id: String,
    val name: String,
    val prefs: Prefs
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Prefs(
        val backgroundImage: String?,
        val backgroundColor: String?
    )
}