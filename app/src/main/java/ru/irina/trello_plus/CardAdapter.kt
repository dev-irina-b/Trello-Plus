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

class CardAdapter(private val items: List<Card>, private val callback: DataCallback<Card>) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCard = items[position]
        val context = holder.itemView.context
        holder.name.text = currentCard.name
        holder.itemView.setOnClickListener {
            callback(currentCard)
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
            holder.checkItemsChecked.text = currentCard.badges.checkItemsChecked.toString()
            holder.checkItems.text = currentCard.badges.checkItems.toString()
        }
        if(currentCard.badges.checkItemsChecked == currentCard.badges.checkItems) {
            val color = ContextCompat.getColor(context, R.color.green)
            val colorStateList = ColorStateList.valueOf(color)
            holder.checklistIcon.imageTintList = colorStateList
            holder.checkItems.setTextColor(color)
            holder.checkItemsChecked.setTextColor(color)
            holder.slash.setTextColor(color)
        } else {
            val color = ContextCompat.getColor(context, R.color.black)
            val colorStateList = ColorStateList.valueOf(color)
            holder.checklistIcon.imageTintList = colorStateList
            holder.checkItems.setTextColor(color)
            holder.checkItemsChecked.setTextColor(color)
            holder.slash.setTextColor(color)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.name
        val descriptionIcon: ImageView = v.descIcon
        val commentCount: TextView = v.commentCount
        val checkItemsChecked: TextView = v.checkItemsChecked
        val checkItems: TextView = v.checkItems
        val slash: TextView = v.slash
        val commentLayout: LinearLayout = v.commentLayout
        val checklistLayout: LinearLayout = v.checklistLayout
        val checklistIcon: ImageView = v.checklistIcon
    }
}