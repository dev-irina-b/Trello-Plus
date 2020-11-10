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
    val labels: List<Label>
): Parcelable

