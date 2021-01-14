package ru.irina.trello_plus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Comment (
    val id: String,
    val data: Data,
    val date: String,
    val memberCreator: MemberCreator,
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        val text: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class MemberCreator(
        val fullName: String,
    )
}