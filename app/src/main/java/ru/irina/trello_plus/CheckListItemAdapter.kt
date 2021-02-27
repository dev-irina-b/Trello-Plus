package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_check_list_item.view.*

class CheckListItemAdapter(private val updateItem: DataCallback<CheckList.Item>,
                           private val deleteItem: DataCallback<CheckList.Item>,
) : ListAdapter<CheckList.Item, CheckListItemAdapter.ViewHolder>(DiffUtilCallback) {

    companion object {
        object DiffUtilCallback : ItemCallback<CheckList.Item>() {
            override fun areItemsTheSame(oldItem: CheckList.Item, newItem: CheckList.Item) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CheckList.Item, newItem: CheckList.Item) = oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_check_list_item, parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCheckListItem = currentList[position]
        val context = holder.itemView.context
        holder.checklistItemName.text = currentCheckListItem.name
        holder.checkBox.isChecked = currentCheckListItem.complete

        holder.checkBox.setOnCheckedChangeListener { _, b ->
            currentCheckListItem.complete = b
            updateItem(currentCheckListItem)
            val hidden = context.getSP().getBoolean("${currentCheckListItem.idChecklist}$CHECKLIST_HIDE_DONE_STATE", false)
            val newItems = if(hidden) currentList.filter { it -> !it.complete  } else currentList
            submitList(newItems)
        }

        holder.checklistItemName.inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        holder.checklistItemName.setHorizontallyScrolling(false)
        holder.checklistItemName.maxLines = Int.MAX_VALUE

        holder.checklistItemName.setOnEditorActionListener { textView, i, keyEvent ->
            currentCheckListItem.name = textView.text.toString()
            if(i == EditorInfo.IME_ACTION_DONE) {
                updateItem(currentCheckListItem)
                holder.itemView.requestFocus()
                holder.itemView.hideKeyboard()
            }
            false
        }

        holder.delete.setOnClickListener {
            deleteItem(currentCheckListItem)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checklistItemName : TextView = v.checklistItemName
        val checkBox : CheckBox = v.checkBox
        val delete: ImageView = v.deleteIcon
    }
}