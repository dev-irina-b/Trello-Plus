package ru.irina.trello_plus

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
data class Card (
    val id: String,
    val idList: String,
    val name: String,
    val desc: String,
    val labels: List<Label>,
    val badges: Badges,
    val idMembers: List<String>,
    val shortUrl: String
): Parcelable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Parcelize
    data class Badges(
        val comments: Int,
        val checkItemsChecked: Int,
        val checkItems: Int,
        val due: String?,
        var dueComplete: Boolean
    ) : Parcelable
}

