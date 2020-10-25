package ru.irina.trello_plus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_card.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall

class CardActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private lateinit var card: Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        getCardData()
        setUpViews()
    }

    private fun getCardData() {
        card = intent.getParcelableExtra("card")!!

        cardTitle.setText(card.name)
        listName.text = card.idList
        description.text = card.desc
        labels.text = card.idLabels.toString()

        boardName.text = intent.getStringExtra("boardName")!!
    }

    private fun setUpViews() {
        save.setOnClickListener {
            updateCardRequest()
        }
    }

    private fun updateCardRequest() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.updateCardName(card.id, cardTitle.text.toString(), token)
            },
            success = {
                toast(R.string.saved)
            }
        )
    }
}