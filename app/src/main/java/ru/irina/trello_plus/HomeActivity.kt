package ru.irina.trello_plus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall

class HomeActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private var boards = listOf<Board>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getBoards()
    }

    private fun getBoards() {
        val token = getSP().getString(SP_LOGIN, "")!!

        makeSafeApiCall(
            request = {
                webService.getBoards(token)
            },
            success = {
                boards = it
                recycler.adapter = BoardAdapter(
                    boards,
                    {
                        onBoardClick(it)
                    }
                )
            }
        )
    }

    private fun onBoardClick(board: Board) {
        val intent = Intent(this, BoardActivity::class.java)
        intent.putExtra("id", board.id)
        startActivity(intent)
    }
}