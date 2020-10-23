package ru.irina.trello_plus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_card.*

class CardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        takeName()
    }

    private fun takeName() {
       header.text = intent.getStringExtra("name")!!
        boardName.text = intent.getStringExtra("boardName")!!
        listName.text = intent.getStringExtra("idList")!!
        description.text = intent.getStringExtra("desc")!!
        labels.text = intent.getStringExtra("idLabels")!!


    }






}