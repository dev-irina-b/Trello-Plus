package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_check_list.view.*

class CheckListAdapter(private val items: List<CheckList>) : RecyclerView.Adapter<CheckListAdapter.ViewHolder>()  {

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
        val context = holder.itemView.context
        holder.checkListTitle.text = currentCheckList.name
        holder.checkListItemRecycler.adapter = CheckListItemAdapter(currentCheckList.checkItems)

        val savedCollapsedState = context.getSP().getBoolean("${currentCheckList.id}$CHECKLIST_COLLAPSED_STATE", false)
        holder.setCollapsedState(savedCollapsedState)

        val savedHideDoneState = context.getSP().getBoolean("${currentCheckList.id}$CHECKLIST_HIDE_DONE_STATE", false)
        holder.setHideDone(currentCheckList.checkItems, savedHideDoneState)

        holder.arrowIcon.setOnClickListener {
            val oldCollapsedState = context.getSP().getBoolean("${currentCheckList.id}$CHECKLIST_COLLAPSED_STATE", false)
            val newCollapsedState = !oldCollapsedState
            holder.setCollapsedState(newCollapsedState)
            context.getSPE().putBoolean("${currentCheckList.id}$CHECKLIST_COLLAPSED_STATE", newCollapsedState).apply()
        }

        holder.checkListOverflow.setOnClickListener {
            val checkListPopup = PopupMenu(context, holder.checkListOverflow)
            val inflater = checkListPopup.menuInflater
            inflater.inflate(R.menu.checklist_popup_menu, checkListPopup.menu)
            checkListPopup.show()

            val hideDone = context.getSP().getBoolean("${currentCheckList.id}$CHECKLIST_HIDE_DONE_STATE", false)
            checkListPopup.menu.findItem(R.id.checkListPopupCheckBox).isChecked = hideDone

            checkListPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.checkListPopupCheckBox -> {
                        val oldState = context.getSP().getBoolean("${currentCheckList.id}$CHECKLIST_HIDE_DONE_STATE", false)
                        val newState = !oldState
                        menuItem.isChecked = newState
                        context.getSPE().putBoolean("${currentCheckList.id}$CHECKLIST_HIDE_DONE_STATE", newState).apply()
                        holder.setHideDone(currentCheckList.checkItems, newState)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checkListTitle: TextView = v.checkListTitle
        val checkListItemRecycler: RecyclerView = v.checkListItemRecycler
        val arrowIcon: ImageView = v.arrowIcon
        val checkListOverflow: ImageView = v.checkListOverflow

        fun setCollapsedState(collapsed: Boolean) {
            arrowIcon.setImageResource( if(collapsed) R.drawable.arrow_down_black_24 else R.drawable.arrow_up_black_24)
            checkListItemRecycler.visibility = if(collapsed) View.GONE else View.VISIBLE
        }

        fun setHideDone(checkItems: List<CheckList.CheckListItem>, hide: Boolean) {
            val newItems = if(hide) checkItems.filter { !it.complete } else checkItems

            checkListItemRecycler.adapter = CheckListItemAdapter(newItems)
        }
    }
}