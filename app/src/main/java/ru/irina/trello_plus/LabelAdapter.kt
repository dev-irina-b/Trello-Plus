package ru.irina.trello_plus

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_choosable_label.view.*

class LabelAdapter(private val items: List<Label>,
                   private val card: Card,
                   private val addLabelCallback: DataCallback<Label>,
                   private val deleteLabelCallback: DataCallback<Label>) : RecyclerView.Adapter<LabelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_choosable_label,
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLabel = items[position]
        val context = holder.itemView.context
        holder.name.text = currentLabel.name

        val labelColor = ContextCompat.getColor(context, when (currentLabel.color) {
            "yellow" -> R.color.yellow
            "orange" -> R.color.orange
            "red" -> R.color.red
            "purple" -> R.color.purple
            "blue" -> R.color.blue
            "sky" -> R.color.sky
            "green" -> R.color.green
            "pink" -> R.color.pink
            else -> R.color.black
        })
        val labelsColorStateList = ColorStateList.valueOf(labelColor)
        holder.constraint.backgroundTintList = labelsColorStateList

        if(card.labels.contains(currentLabel)) {
            holder.done.visibility = View.VISIBLE
        } else holder.done.visibility = View.GONE

        holder.constraint.setOnClickListener {
            if(holder.done.isVisible) {
                deleteLabelCallback(currentLabel)
                holder.done.visibility = View.GONE
            }
            else if(holder.done.isGone){
                addLabelCallback(currentLabel)
                holder.done.visibility = View.VISIBLE
            }
        }
    }
    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.name
        val constraint: ConstraintLayout = v.constraint
        val done: ImageView = v.done
    }
}