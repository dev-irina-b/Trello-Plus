package ru.irina.trello_plus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            boards = webService.getBoards(token)
            recycler.adapter = BoardAdapter(boards)
        }
    }
}