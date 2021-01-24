package ru.irina.trello_plus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CheckList (
    val id: String,
    val name: String,
    val checkItems: List<Item>
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Item (
        val name: String,
        var state: String,
    ) {
        val complete: Boolean get() = state == "complete"
    }
}