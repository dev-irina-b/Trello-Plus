package ru.irina.trello_plus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CheckList (
    val id: String,
    val name: String,
    val checkItems: List<Item>
) {
    var addItem: String = ""

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Item (
        val id: String,
        val idChecklist: String,
        var name: String,
        var state: String,
    ) {
        var complete: Boolean
            get() = state == "complete"
            set(value) {
                state = if(value) "complete" else "incomplete"
            }
    }
}