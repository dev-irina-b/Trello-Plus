package ru.irina.trello_plus

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*
import java.text.SimpleDateFormat
import java.util.*

class CardAdapter(private val items: List<Card>, private val callback: DataCallback<Card>) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    private companion object {
        const val DAY_MS = 1000*60*60*24

        const val PARSING_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        const val FORMATTING_PATTERN = "d MMM"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_card,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCard = items[position]
        val context = holder.itemView.context
        holder.name.text = currentCard.name
        holder.itemView.setOnClickListener {
            callback(currentCard)
        }

        if(currentCard.badges.due.isNullOrBlank()) {
            holder.timeLayout.visibility = View.GONE
        } else {
            holder.timeLayout.visibility = View.VISIBLE

            val cardDueDate = SimpleDateFormat(PARSING_PATTERN, Locale.getDefault()).parse(currentCard.badges.due)
            if(cardDueDate == null) {
                holder.timeDue.text = ""
                return
            }
            val formattedDate = SimpleDateFormat(FORMATTING_PATTERN, Locale.getDefault()).format(cardDueDate)
            holder.timeDue.text = formattedDate.toLowerCase(Locale.getDefault())

            val currentDate = Date()
            val rest = cardDueDate.time - currentDate.time

            val color = ContextCompat.getColor(context, when {
                currentCard.badges.dueComplete -> R.color.green
                rest <= 0 -> R.color.red
                rest in 0..DAY_MS -> R.color.darkYellow
                else -> R.color.black
            })

            val colorStateList = ColorStateList.valueOf(color)

            holder.timeIcon.imageTintList = colorStateList
            holder.timeDue.setTextColor(color)
        }

        holder.descriptionIcon.visibility = if(currentCard.desc.isBlank()) View.GONE  else View.VISIBLE

        if(currentCard.badges.comments == 0) {
            holder.commentLayout.visibility = View.GONE
        } else {
            holder.commentLayout.visibility = View.VISIBLE
            holder.commentCount.text = currentCard.badges.comments.toString()
        }
        if(currentCard.badges.checkItems == 0) {
            holder.checklistLayout.visibility = View.GONE
        } else {
            holder.checklistLayout.visibility = View.VISIBLE
            holder.checkListCounter.text = "${currentCard.badges.checkItemsChecked}/${currentCard.badges.checkItems}"
        }
        if(currentCard.badges.checkItemsChecked == currentCard.badges.checkItems) {
            val color = ContextCompat.getColor(context, R.color.green)
            val colorStateList = ColorStateList.valueOf(color)
            holder.checklistIcon.imageTintList = colorStateList
            holder.checkListCounter.setTextColor(color)
        } else {
            val color = ContextCompat.getColor(context, R.color.black)
            val colorStateList = ColorStateList.valueOf(color)
            holder.checklistIcon.imageTintList = colorStateList
            holder.checkListCounter.setTextColor(color)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.name
        val timeLayout: LinearLayout = v.timeLayout
        val timeIcon: ImageView = v.timeIcon
        val timeDue: TextView = v.timeDue
        val descriptionIcon: ImageView = v.descIcon
        val commentCount: TextView = v.commentCount
        val checkListCounter: TextView = v.checkListCounter
        val commentLayout: LinearLayout = v.commentLayout
        val checklistLayout: LinearLayout = v.checklistLayout
        val checklistIcon: ImageView = v.checklistIcon
    }
}