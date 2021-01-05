package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_check_list.view.*

class CheckListAdapter(private val items: List<CheckList>) : RecyclerView.Adapter<CheckListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_check_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCheckList = items[position]
        holder.checkListTitle.text = currentCheckList.name
        holder.checkListItemRecycler.adapter = CheckListItemAdapter(currentCheckList.checkItems)
        holder.arrowIcon.setOnClickListener {
            currentCheckList.collapsed = !currentCheckList.collapsed
            holder.setCollapsedState(currentCheckList.collapsed)
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checkListTitle: TextView = v.checkListTitle
        val checkListItemRecycler: RecyclerView = v.checkListItemRecycler
        val arrowIcon: ImageView = v.arrowIcon

        fun setCollapsedState(collapsed: Boolean) {
            arrowIcon.setImageResource( if(collapsed) R.drawable.arrow_down_black_24 else R.drawable.arrow_up_black_24)
            checkListItemRecycler.visibility = if(collapsed) View.GONE else View.VISIBLE
        }
    }
}