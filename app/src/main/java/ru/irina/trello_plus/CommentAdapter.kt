package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_comment.view.*
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(
    private val items: MutableList<Comment>,
    private val deleteCallback: DataCallback<Comment>,
    private val updateCallback: DataCallback<Comment>
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentComment = items[position]
        val context = holder.itemView.context
        holder.userName.text = currentComment.memberCreator.fullName
        holder.commentText.text = currentComment.data.text

        val commentParsingDate =
            SimpleDateFormat(DATE_PARSING_PATTERN, Locale.getDefault()).parse(currentComment.date)
        if (commentParsingDate == null) {
            holder.date.text = ""
            return
        }
        val formattedMonth =
            SimpleDateFormat(DATE_FORMATTING_PATTERN, Locale.getDefault()).format(commentParsingDate)
        val formattedTime =
            SimpleDateFormat(TIME_FORMATTING_PATTERN, Locale.getDefault()).format(commentParsingDate)
        val commentDate =
            "${formattedMonth.toLowerCase(Locale.getDefault())} ${context.getString(R.string.at)} $formattedTime"
        holder.date.text = commentDate

        val commentPopup = PopupMenu(context, holder.commentOverflow)
        val inflater = commentPopup.menuInflater
        inflater.inflate(R.menu.comment_popup_menu, commentPopup.menu)
        commentPopup.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.delete -> {
                    deleteCallback(currentComment)
                    true
                }
                else -> true
            }
        }

        holder.commentOverflow.setOnClickListener {
            commentPopup.show()
        }
        holder.done.setOnClickListener {
            currentComment.data.text = holder.commentText.text.toString()
            updateCallback(currentComment)
            holder.itemView.requestFocus()
            holder.itemView.hideKeyboard()
        }

        holder.commentText.setOnFocusChangeListener { _, b ->
            holder.done.visibility = if(b) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val userName: TextView = v.userName
        val commentText: TextView = v.commentText
        val date: TextView = v.date
        val commentOverflow: ImageView = v.commentOverflow
        val done: ImageView = v.done
    }
}