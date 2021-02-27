package ru.irina.trello_plus

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

interface CardProcessor {

    fun updateDateColors(context: Context, card: Card, dateView: TextView, icon: ImageView) {
        getCardDueDate(card)?.let {
            val currentDate = Date()
            val rest = it.time - currentDate.time

            val color = ContextCompat.getColor(
                context, when {
                    card.badges.dueComplete -> R.color.green
                    rest <= 0 -> R.color.red
                    rest in 0..DAY_MS -> R.color.darkYellow
                    else -> R.color.black
                }
            )
            val colorStateList = ColorStateList.valueOf(color)
            icon.imageTintList = colorStateList
            dateView.setTextColor(color)
        }
    }

    fun getCardDueDate(card: Card): Date? = card.badges.due?.run {
        SimpleDateFormat(DATE_PARSING_PATTERN, Locale.getDefault()).parse(this)
    }

    fun setDate(context: Context, card: Card, viewToShow: View, dateView: TextView, icon: ImageView, fallbackText: String = ""): Pair<String, String> {
        if(card.badges.due.isNullOrBlank()) {
            viewToShow.visibility = View.GONE
            dateView.text = fallbackText
            return Pair("", "")
        } else {
            Log.i("DUE", "${card.badges.due}")
            viewToShow.visibility = View.VISIBLE

            val cardDueDate = getCardDueDate(card)
            Log.i("DUE", "$cardDueDate")
            if (cardDueDate == null) {
                dateView.text = fallbackText
                return Pair("", "")
            }
            val formattedDate = SimpleDateFormat(DATE_FORMATTING_PATTERN, Locale.getDefault()).format(cardDueDate)
            Log.i("DUE", "$formattedDate")
            val formattedTime = SimpleDateFormat(TIME_FORMATTING_PATTERN, Locale.getDefault()).format(cardDueDate)
            dateView.text = "${formattedDate.toLowerCase(Locale.getDefault())} ${context.getString(R.string.at)} $formattedTime"
            Log.i("DUE", "${dateView.text}")

            updateDateColors(context, card, dateView, icon)

            return Pair(formattedDate, formattedTime)
        }
    }
}