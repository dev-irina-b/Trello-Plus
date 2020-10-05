package ru.irina.trello_plus

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.android.material.textfield.TextInputLayout

fun Context.getSP(): SharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

fun Context.getSPE(): SharedPreferences.Editor = getSP().edit()

val TextInputLayout.text; get() = editText!!.text.toString()