package ru.irina.trello_plus

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_board.view.*
import java.text.SimpleDateFormat
import java.util.*

class BoardAdapter(private val items: List<Board>, private val callback: DataCallback<Board>) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentBoard = items[position]
        holder.name.text = currentBoard.name
        holder.itemView.setOnClickListener {
            callback(currentBoard)
        }
        Glide
            .with(holder.itemView.context)
            .load(currentBoard.prefs.backgroundImage)
            .into(holder.boardImage)

        currentBoard.prefs.backgroundColor?.apply {
            holder.boardImage.setBackgroundColor(Color.parseColor(this))
        }

        if(currentBoard.dateLastActivity.isNullOrBlank()||currentBoard.dateLastView.isNullOrBlank())
            holder.boardChangedIcon.visibility = View.GONE
        else {
            val dateLastActivity =
                SimpleDateFormat(DATE_PARSING_PATTERN, Locale.getDefault()).parse(currentBoard.dateLastActivity)!!
            val dateLastView =
                SimpleDateFormat(DATE_PARSING_PATTERN, Locale.getDefault()).parse(currentBoard.dateLastView)!!
            if(dateLastActivity.time > dateLastView.time)
                holder.boardChangedIcon.visibility = View.VISIBLE
            else
                holder.boardChangedIcon.visibility = View.GONE
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.name
        val boardImage: ImageView = v.boardImage
        val boardChangedIcon: ImageView = v.boardChangedIcon
    }
}