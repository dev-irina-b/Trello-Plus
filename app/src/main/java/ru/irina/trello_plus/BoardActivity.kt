package ru.irina.trello_plus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall

class BoardActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private var cards = listOf<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        getCards()
    }

    private fun getCards() {
        val token = getSP().getString(SP_LOGIN, "")!!

        makeSafeApiCall(
            request = {
                val id = intent.getStringExtra("id")!!
                webService.getCards(id, token)
            },
            success = {
                cards = it
                recycler.adapter = CardAdapter(cards,  {
                    onCardClick(it )
                })
            }
        )
    }

    private fun onCardClick(card: Card) {
        val intent = Intent(this, CardActivity::class.java)

        intent.putExtra("card", card)
        intent.putExtra("boardName", this.intent.getStringExtra("boardName"))

        startActivity(intent)
    }

}