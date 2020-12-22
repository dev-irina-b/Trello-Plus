package ru.irina.trello_plus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_comment.view.*
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(private val items: List<Comment>,) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

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
        holder.comment.text = currentComment.data.text

        val commentParsingDate =
            SimpleDateFormat(DATE_PARSING_PATTERN, Locale.getDefault()).parse(currentComment.date)
        if (commentParsingDate == null) {
            holder.date.text = ""
            return
        }
        val formattedMonth =
            SimpleDateFormat(COMMENT_DATE_FORMATTING_PATTERN, Locale.getDefault()).format(commentParsingDate)
        val formattedTime =
            SimpleDateFormat(COMMENT_TIME_FORMATTING_PATTERN, Locale.getDefault()).format(commentParsingDate)
        val commentDate =
            "${formattedMonth.toLowerCase(Locale.getDefault())} ${context.getString(R.string.at)} $formattedTime"
        holder.date.text = commentDate
    }

    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val userName: TextView = v.userName
        val comment: TextView = v.comment
        val date: TextView = v.date
    }
}