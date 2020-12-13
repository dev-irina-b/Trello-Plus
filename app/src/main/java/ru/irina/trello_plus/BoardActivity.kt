package ru.irina.trello_plus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.fragment_column.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall

class BoardActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private var cards = listOf<Card>()
    private var columns = listOf<Column>()
    private var members = listOf<Member>()

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
                updateColumnRequest()
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
                pager.adapter = ColumnPagerAdapter(supportFragmentManager, columns, cards, members)
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
                getCards()
            }
        )
    }

    private fun getBoardMembers() {
        val token = getSP().getString(SP_LOGIN, "")!!
        makeSafeApiCall(
            request = {
                val id = intent.getStringExtra("id")!!
                webService.getBoardMembers(id, token)
            },
            success = {
                members = it
                getColumns()
            }
        )
    }

    private fun onAddClick() {
        val intent = Intent(this, NewCardActivity::class.java)
        intent.putParcelableArrayListExtra("columns", ArrayList(columns))
        startActivity(intent)
    }

    private fun updateColumnRequest() {
        makeSafeApiCall(
            request = {
                val id = columns[pager.currentItem].id
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.updateColumnName(id, columnTitle.text.toString(), token)
            },
            success = {
                toast(R.string.saved)
            }
        )
    }

    override fun onResume() {
        super.onResume()
        getBoardMembers()
    }
}