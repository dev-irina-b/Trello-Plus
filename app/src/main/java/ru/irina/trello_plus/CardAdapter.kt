package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*

class CardAdapter(private val items: List<Card>, private val callback: DataCallback<Card>) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCard = items[position]
        holder.name.text = currentCard.name
        holder.itemView.setOnClickListener {
            callback(currentCard)
        }
        holder.descriptionIcon.visibility = when (currentCard.desc){
           "" -> View.GONE
            else -> View.VISIBLE
        }
        holder.commentIcon.visibility = when(currentCard.badges.comments) {
            0 -> View.GONE
            else -> View.VISIBLE
        }
        holder.commentCount.visibility = when(currentCard.badges.comments) {
            0 -> View.GONE
            else -> View.VISIBLE
        }
        holder.commentCount.text = currentCard.badges.comments.toString()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.name
        val descriptionIcon: ImageView = v.descIcon
        val commentIcon: ImageView = v.commentIcon
        val commentCount: TextView = v.commentCount

    }
}