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
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.item_card.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CardAdapter(private val items: List<Card>,
                  private val boardMembers: List<Member>,
                  private val callback: DataCallback<Card>) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

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

        if (currentCard.badges.due.isNullOrBlank()) {
            holder.timeLayout.visibility = View.GONE
        } else {
            holder.timeLayout.visibility = View.VISIBLE

            val cardDueDate =
                SimpleDateFormat(DATE_PARSING_PATTERN, Locale.getDefault()).parse(currentCard.badges.due)
            if (cardDueDate == null) {
                holder.timeDue.text = ""
                return
            }
            val formattedDate =
                SimpleDateFormat(DATE_FORMATTING_PATTERN, Locale.getDefault()).format(cardDueDate)
            holder.timeDue.text = formattedDate.toLowerCase(Locale.getDefault())

            val currentDate = Date()
            val rest = cardDueDate.time - currentDate.time

            val color = ContextCompat.getColor(
                context, when {
                    currentCard.badges.dueComplete -> R.color.green
                    rest <= 0 -> R.color.red
                    rest in 0..DAY_MS -> R.color.darkYellow
                    else -> R.color.black
                }
            )

            val colorStateList = ColorStateList.valueOf(color)

            holder.timeIcon.imageTintList = colorStateList
            holder.timeDue.setTextColor(color)
        }

        holder.descriptionIcon.visibility =
            if (currentCard.desc.isBlank()) View.GONE else View.VISIBLE

        if (currentCard.badges.comments == 0) {
            holder.commentLayout.visibility = View.GONE
        } else {
            holder.commentLayout.visibility = View.VISIBLE
            holder.commentCount.text = currentCard.badges.comments.toString()
        }
        if (currentCard.badges.checkItems == 0) {
            holder.checklistLayout.visibility = View.GONE
        } else {
            holder.checklistLayout.visibility = View.VISIBLE
            holder.checkListCounter.text =
                "${currentCard.badges.checkItemsChecked}/${currentCard.badges.checkItems}"
        }
        if (currentCard.badges.checkItemsChecked == currentCard.badges.checkItems) {
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

        when (currentCard.idMembers.size){
            0 -> {
                holder.firstMember.visibility = View.GONE
                holder.secondMember.visibility = View.GONE
                holder.thirdMember.visibility = View.GONE
                holder.otherMember.visibility = View.GONE
            }
            1 -> {
                holder.firstMember.visibility = View.VISIBLE
                holder.firstMember.text = boardMembers.first { it.id == currentCard.idMembers[0]}.initials
                holder.secondMember.visibility = View.GONE
                holder.thirdMember.visibility = View.GONE
                holder.otherMember.visibility = View.GONE
            }
            2 -> {
                holder.firstMember.visibility = View.VISIBLE
                holder.firstMember.text = boardMembers.first { it.id == currentCard.idMembers[0]}.initials
                holder.secondMember.visibility = View.VISIBLE
                holder.secondMember.text = boardMembers.first { it.id == currentCard.idMembers[1]}.initials
                holder.thirdMember.visibility = View.GONE
                holder.otherMember.visibility = View.GONE
            }
            3 -> {
                holder.firstMember.visibility = View.VISIBLE
                holder.firstMember.text = boardMembers.first { it.id == currentCard.idMembers[0]}.initials
                holder.secondMember.visibility = View.VISIBLE
                holder.secondMember.text = boardMembers.first { it.id == currentCard.idMembers[1]}.initials
                holder.thirdMember.visibility = View.VISIBLE
                holder.thirdMember.text = boardMembers.first { it.id == currentCard.idMembers[2]}.initials
                holder.otherMember.visibility = View.VISIBLE
            }
        }

        val currentUserId = context.getSP().getString(SP_USER_ID, "")!!
        val currentUserSubscribed = currentCard.idMembers.contains(currentUserId)
        holder.watchIcon.visibility = if(currentUserSubscribed) View.VISIBLE else View.GONE

        holder.labelsFlexBox.removeAllViews()
        currentCard.labels.forEach {
            val labelView = View(context)
            val labelColor = ContextCompat.getColor(context, when (it.color) {
                    "yellow" -> R.color.yellow
                    "orange" -> R.color.orange
                    "red" -> R.color.red
                    "purple" -> R.color.purple
                    "blue" -> R.color.blue
                    "sky" -> R.color.sky
                    "lime" -> R.color.lime
                    "pink" -> R.color.pink
                    else -> R.color.black
                })
            val labelsColorStateList = ColorStateList.valueOf(labelColor)
            labelView.backgroundTintList = labelsColorStateList
            labelView.background =  ContextCompat.getDrawable(context, R.drawable.item_card_labels_bg_drawable)
            val params = FlexboxLayout.LayoutParams(
                context.resources.getDimension(R.dimen.item_card_label_width).roundToInt(),
                context.resources.getDimension(R.dimen.item_card_label_height).roundToInt()
            )
            holder.labelsFlexBox.addView(labelView, params)
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
        val firstMember: TextView = v.firstMember
        val secondMember: TextView = v.secondMember
        val thirdMember: TextView = v.thirdMember
        val otherMember: TextView = v.otherMember
        val watchIcon: ImageView = v.watchIcon
        val labelsFlexBox: FlexboxLayout = v.labelsFlexbox
    }
}