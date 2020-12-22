package ru.irina.trello_plus

import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_card.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall

class CardActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private lateinit var card: Card
    private lateinit var popup: PopupMenu
    private var comments = listOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        getCardData()
        setUpViews()
        getComments()
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
        backspace.setOnClickListener {
            finish()
        }
        createPopup(overflow)
        overflow.setOnClickListener {
            popup.show()
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete -> {
                        deleteCard()
                        true
                    }
                    R.id.share -> {
                       shareCard()
                        true
                    }
                    else -> false
                }
            }
        }
        done.setOnClickListener {
            addComment()
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

    private fun createPopup(view: View) {
        popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.popup_menu, popup.menu)
    }

    private fun shareCard() {
        val textMessage = "${card.name}\n${card.shortUrl}"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textMessage)
            type = "text/plain"
        }
        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(sendIntent)
        }
    }

    private fun getComments() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.getComments(card.id, token)
            },
            success = {
                comments = it
                recycler.adapter = CommentAdapter(comments)
            }
        )
    }

    private fun addComment() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.addComment(card.id,comment.text.toString(),token)
            },
            success = {
                toast(R.string.saved)
            }
        )
    }
}