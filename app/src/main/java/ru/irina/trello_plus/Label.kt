package ru.irina.trello_plus

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Label (
    val id: String,
    val idBoard: String,
    val name: String,
    val color: String,
) : Parcelable