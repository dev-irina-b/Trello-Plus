package ru.irina.trello_plus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_card.*

class CardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        getCardData()
    }

    private fun getCardData() {
        val card = intent.getParcelableExtra<Card>("card")!!

        header.text = card.name
        listName.text = card.idList
        description.text = card.desc
        labels.text = card.idLabels.toString()

        boardName.text = intent.getStringExtra("boardName")!!
    }
}