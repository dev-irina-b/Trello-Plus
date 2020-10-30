package ru.irina.trello_plus

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_card.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall

class NewCardActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private var columns = listOf<Column>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_card)

        setUpViews()
    }

    private fun setUpViews() {
        columns = intent.getParcelableArrayListExtra<Column>("columns")!!
        val columnNames = columns.map {
            it.name
        }

        columnSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, columnNames)

        create.setOnClickListener {
            createNewCard()
        }
    }

    private fun createNewCard() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                val index = columnSpinner.selectedItemPosition
                val selectedColumn = columns[index]
                val columnId = selectedColumn.id

                webService.createNewCard(
                    cardTitle.text.toString(),
                    cardDescription.text.toString(),
                    columnId,
                    token
                )
            },
            success = {
                toast(R.string.saved)
                finish()
            }
        )
    }
}