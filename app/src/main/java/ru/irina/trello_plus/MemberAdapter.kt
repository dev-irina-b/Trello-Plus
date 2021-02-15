package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_choosable_member.view.*

class MemberAdapter(private val items: List<Member>,) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_choosable_member,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMember = items[position]
        val context = holder.itemView.context

        holder.memberIcon.text = currentMember.initials
        holder.fullName.text = currentMember.fullName
        holder.userName.text = currentMember.username

        holder.memberConstraint.setOnClickListener {
            currentMember.checked = holder.done.visibility != View.VISIBLE
            holder.done.visibility = if(currentMember.checked) View.VISIBLE else View.GONE
        }
        holder.done.visibility = if(currentMember.checked) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val memberIcon: TextView = v.memberIcon
        val fullName: TextView = v.fullName
        val userName: TextView = v.userName
        val done: ImageView = v.done
        val memberConstraint: ConstraintLayout =  v.memberConstraint
    }
}