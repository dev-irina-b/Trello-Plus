package ru.irina.trello_plus

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_card.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall
import java.text.SimpleDateFormat
import java.util.*

class CardActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private lateinit var card: Card
    private lateinit var popup: PopupMenu
    private var comments = listOf<Comment>()
    private var boardMembers = listOf<Member>()
    private var checklists = listOf<CheckList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        getCardData()
        setUpViews()
    }

    private fun getCardData() {
        card = intent.getParcelableExtra("card")!!
        boardMembers = intent.getParcelableArrayListExtra("boardMembers")!!

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
        back.setOnClickListener {
            finish()
        }
        createPopup(overflow)
        overflow.setOnClickListener {
            popup.show()
        }
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
        sendComment.setOnClickListener {
            addComment()
        }
        getComments()
        getMembers()
        setDate()
        setUpDateCheckBox()
        getChecklists()
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
                commentRecycler.adapter = CommentAdapter(comments)
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

    private fun getMembers() {
        when (card.idMembers.size) {
            1 -> {
                contactIcon.visibility = View.VISIBLE
                firstMember.visibility = View.VISIBLE
                firstMember.text = boardMembers.first { it.id == card.idMembers[0]}.initials
                secondMember.visibility = View.GONE
            }
            2 -> {
                contactIcon.visibility = View.VISIBLE
                firstMember.visibility = View.VISIBLE
                firstMember.text = boardMembers.first { it.id == card.idMembers[0]}.initials
                secondMember.visibility = View.VISIBLE
                secondMember.text = boardMembers.first { it.id == card.idMembers[1]}.initials
            }
            3 -> {
                contactIcon.visibility = View.VISIBLE
                firstMember.visibility = View.VISIBLE
                firstMember.text = boardMembers.first { it.id == card.idMembers[0]}.initials
                secondMember.visibility = View.VISIBLE
                secondMember.text = boardMembers.first { it.id == card.idMembers[1]}.initials
                thirdMember.visibility = View.VISIBLE
                thirdMember.text = boardMembers.first { it.id == card.idMembers[2]}.initials
                otherMember.visibility = View.VISIBLE
            }
        }
    }
    
    private fun setDate() {
        if (!card.badges.due.isNullOrBlank()) {
            calendarIcon.visibility = View.VISIBLE
            cardDueTime.visibility = View.VISIBLE
            checkBox.visibility = View.VISIBLE

            val cardDueDate =
                SimpleDateFormat(DATE_PARSING_PATTERN, Locale.getDefault()).parse(card.badges.due!!)
            if (cardDueDate == null) {
                cardDueTime.text = ""
                return
            }
            val formattedDate =
                SimpleDateFormat(DATE_FORMATTING_PATTERN, Locale.getDefault()).format(cardDueDate)
            val formattedTime =
                SimpleDateFormat(TIME_FORMATTING_PATTERN, Locale.getDefault()).format(cardDueDate)
            val commentDate =
                "${formattedDate.toLowerCase(Locale.getDefault())} ${this.getString(R.string.at)} $formattedTime"
            cardDueTime.text = commentDate

            val currentDate = Date()
            val rest = cardDueDate.time - currentDate.time

            val color = ContextCompat.getColor(
                this, when {
                    card.badges.dueComplete -> R.color.green
                    rest <= 0 -> R.color.red
                    rest in 0..DAY_MS -> R.color.darkYellow
                    else -> R.color.black
                }
            )

            val colorStateList = ColorStateList.valueOf(color)

            calendarIcon.imageTintList = colorStateList
            cardDueTime.setTextColor(color)
        }
    }

    private fun setUpDateCheckBox() {
        checkBox.isChecked = card.badges.dueComplete

        checkBox.setOnClickListener {
            card.badges.dueComplete = checkBox.isChecked

            makeSafeApiCall(
                request = {
                    val token = getSP().getString(SP_LOGIN, "")!!
                    webService.updateCardDueComplete(card.id, card.badges.dueComplete, token)
                },
                success = {}
            )
        }
    }

    private fun getChecklists() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.getChecklists(card.id, token)
            },
            success = {
                checklists = it
                checkListRecycler.adapter = CheckListAdapter(checklists)
            }
        )
    }
}