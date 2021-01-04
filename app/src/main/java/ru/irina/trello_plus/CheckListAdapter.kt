package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checkListTitle : TextView = v.checkListTitle
        val checkListItemRecycler : RecyclerView = v.checkListItemRecycler
    }
}