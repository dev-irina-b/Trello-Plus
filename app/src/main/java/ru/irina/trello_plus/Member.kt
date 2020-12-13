package ru.irina.trello_plus

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
data class Member (
    val id: String,
    val fullName: String
) : Parcelable {

    @IgnoredOnParcel
    val initials = fullName.split(" ").run {
        if(this.size == 1) {
            "${this[0][0].toUpperCase()}"
        } else
            "${this[0][0].toUpperCase()}${this[1][0].toUpperCase()}"
    }
}