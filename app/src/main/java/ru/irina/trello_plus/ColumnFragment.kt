package ru.irina.trello_plus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_column.*

class ColumnFragment(private val currentColumnCards: List<Card>,
                     private val boardMembers: List<Member>,
                     private val currentColumnName: String) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_column, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.adapter = CardAdapter(currentColumnCards, boardMembers) {
            onCardClick(it)
        }

        columnTitle.setText(currentColumnName)
    }

    private fun onCardClick(card: Card) {
        val intent = Intent(activity, CardActivity::class.java)
        intent.putExtra("card", card)
        intent.putExtra("boardName", activity?.intent?.getStringExtra("boardName"))
        intent.putExtra("columnName", currentColumnName )
        intent.putParcelableArrayListExtra("boardMembers", ArrayList(boardMembers))
        startActivity(intent)
    }

}