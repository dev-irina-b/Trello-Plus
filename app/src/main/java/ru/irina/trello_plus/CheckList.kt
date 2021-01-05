package ru.irina.trello_plus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CheckList (
    val name: String,
    val checkItems: List<CheckListItem>
) {
    var collapsed: Boolean = false
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class CheckListItem (
        val name: String,
    )
}