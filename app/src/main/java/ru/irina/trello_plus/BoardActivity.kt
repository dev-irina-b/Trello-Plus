package ru.irina.trello_plus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_home.recycler
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall

class BoardActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private var cards = listOf<Card>()
    private var columns = listOf<Column>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        setUpViews()
    }

    private fun setUpViews() {
        boardTitle.setText(intent.getStringExtra("boardName"))
        save.setOnClickListener {
            updateBoardRequest()
        }
        add.setOnClickListener {
            onAddClick()
        }
    }

    private fun updateBoardRequest() {
        makeSafeApiCall(
            request = {
                val idBoard = intent.getStringExtra("id")!!
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.updateBoardName(idBoard, boardTitle.text.toString(), token)
            },
            success = {
                toast(R.string.saved)
            }
        )
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

    private fun getColumns() {
        val token = getSP().getString(SP_LOGIN, "")!!
        makeSafeApiCall(
            request = {
                val id = intent.getStringExtra("id")!!
                webService.getColumns(id, token)
            },
            success = {
                columns = it
            }
        )
    }

    private fun onAddClick() {
        val intent = Intent(this, NewCardActivity::class.java)
        intent.putParcelableArrayListExtra("columns", ArrayList(columns))
        startActivity(intent)
    }

    private fun onCardClick(card: Card) {
        val intent = Intent(this, CardActivity::class.java)

        val cardColumn = columns.find {
            it.id == card.idList
        }
        val columnName = cardColumn?.name ?: "unknown"

        intent.putExtra("card", card)
        intent.putExtra("boardName", this.intent.getStringExtra("boardName"))
        intent.putExtra("columnName", columnName)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        getCards()
        getColumns()
    }
}