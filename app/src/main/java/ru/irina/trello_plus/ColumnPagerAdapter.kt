package ru.irina.trello_plus

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ColumnPagerAdapter(manager: FragmentManager,
                         private val columns: List<Column>,
                         private  val cards: List<Card>,
                         private  val members: List<Member>,
                         private  val labels: List<Label>)
    : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = columns.size

    override fun getItem(position: Int): Fragment {
        val currentColumn = columns[position]
        val currentColumnCards = cards.filter {
            it.idList == currentColumn.id
       }
        val currentColumnName = currentColumn.name
        val fragment = ColumnFragment()
        val args = Bundle()
        args.putParcelableArrayList("currentColumnCards", ArrayList(currentColumnCards))
        args.putParcelableArrayList("boardMembers", ArrayList(members))
        args.putParcelableArrayList("boardLabels", ArrayList(labels))
        args.putString("currentColumnName", currentColumnName)
        fragment.arguments = args
        return fragment
    }
}