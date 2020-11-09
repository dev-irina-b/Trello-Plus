package ru.irina.trello_plus

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ColumnPagerAdapter(manager: FragmentManager, private val columns: List<Column>, private  val cards: List<Card> )
    : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = columns.size

    override fun getItem(position: Int): Fragment {
        val currentColumn = columns[position]
        val currentColumnCards = cards.filter {
            it.idList == currentColumn.id
       }
        val currentColumnName = currentColumn.name
        return ColumnFragment(currentColumnCards, currentColumnName)
    }
}