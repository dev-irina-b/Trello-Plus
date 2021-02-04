package ru.irina.trello_plus

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

fun Context.getSP(): SharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

fun Context.getSPE(): SharedPreferences.Editor = getSP().edit()

val TextInputLayout.text get() = editText!!.text.toString()

fun AppCompatActivity.toast(@StringRes stringRes: Int) {
    Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()
}

fun View.hideKeyboard(){
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

val Int.twoDigitString get() = if(this < 10) "0$this" else "$this"