package ru.irina.trello_plus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
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
        columnName.text = intent.getStringExtra("columnName")
        description.setText(card.desc)
        card.labels.forEach {
            val chip = Chip(this)
            chip.text = it.name
            chip.setChipBackgroundColorResource(
                when (it.color) {
                    "yellow" -> R.color.yellow
                    "orange" -> R.color.orange
                    "red" -> R.color.red
                    "purple" -> R.color.purple
                    "blue" -> R.color.blue
                    "sky" -> R.color.sky
                    "lime" -> R.color.lime
                    "pink" -> R.color.pink
                    "black" -> R.color.black
                    else -> R.color.black
                }
            )
            chip.setTextColor(resources.getColor(R.color.white) )
            labels.addView(chip)
        }
        boardName.text = intent.getStringExtra("boardName")!!

    }

    private fun setUpViews() {
        save.setOnClickListener {
            updateCardRequest()
        }
        delete.setOnClickListener {
            deleteCard()
        }
    }

    private fun updateCardRequest() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.updateCard(card.id, cardTitle.text.toString(),description.text.toString(), token)
            },
            success = {
                toast(R.string.saved)
            }
        )
    }

    private fun deleteCard() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.deleteCard(card.id, token)
            },
            success = {
                toast(R.string.deleted)
                finish()
            }
        )
    }
}