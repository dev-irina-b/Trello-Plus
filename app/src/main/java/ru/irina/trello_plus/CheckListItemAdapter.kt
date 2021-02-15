package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_check_list_item.view.*

class CheckListItemAdapter(private val items: List<CheckList.Item>,
                           private val updateItem: DataCallback<CheckList.Item>
                           ) : RecyclerView.Adapter<CheckListItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_check_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCheckListItem = items[position]
        holder.checklistItemName.text = currentCheckListItem.name
        holder.checkBox.isChecked = currentCheckListItem.complete

        holder.checkBox.setOnCheckedChangeListener { _, b ->
            currentCheckListItem.complete = b
            updateItem(currentCheckListItem)
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checklistItemName : TextView = v.checklistItemName
        val checkBox : CheckBox = v.checkBox
    }
}