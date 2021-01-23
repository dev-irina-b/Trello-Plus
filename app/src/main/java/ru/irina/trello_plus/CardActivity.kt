package ru.irina.trello_plus

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_card.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall
import java.text.SimpleDateFormat
import java.util.*

class CardActivity : AppCompatActivity() {

    private val webService = WebService.build()
    private lateinit var card: Card
    private lateinit var cardPopup: PopupMenu
    private var comments = mutableListOf<Comment>()
    private var boardMembers = listOf<Member>()
    private var checklists = listOf<CheckList>()
    private var boardLabels = listOf<Label>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        getCardData()
        setUpViews()
    }

    private fun getCardData() {
        card = intent.getParcelableExtra("card")!!
        boardMembers = intent.getParcelableArrayListExtra("boardMembers")!!
        boardLabels = intent.getParcelableArrayListExtra("boardLabels")!!
        cardTitle.setText(card.name)
        columnName.text = intent.getStringExtra("columnName")
        description.setText(card.desc)
        if(card.labels.isNotEmpty()) {
            labels.visibility = View.VISIBLE
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
                        "green" -> R.color.green
                        "pink" -> R.color.pink
                        "black" -> R.color.black
                        else -> R.color.black
                    }
                )
                chip.setTextColor(resources.getColor(R.color.white))
                labels.addView(chip)
            }
        } else labelText.visibility = View.VISIBLE
        boardName.text = intent.getStringExtra("boardName")!!
    }

    private fun setUpViews() {
        save.setOnClickListener {
            updateCardRequest()
        }
        back.setOnClickListener {
            finish()
        }
        createCardPopup(overflow)
        overflow.setOnClickListener {
            cardPopup.show()
        }
        cardPopup.setOnMenuItemClickListener {
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
        labelsBg.setOnClickListener {
            createAlertDialog()
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

    private fun createCardPopup(view: View) {
        cardPopup = PopupMenu(this, view)
        val inflater: MenuInflater = cardPopup.menuInflater
        inflater.inflate(R.menu.card_popup_menu, cardPopup.menu)
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
                comments = it.toMutableList()
                commentRecycler.adapter = CommentAdapter(comments, { deleteComment(it) }, { updateComment(it) })
            }
        )
    }

    private fun addComment() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.addComment(card.id,commentText.text.toString(),token)
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
                checkListRecycler.adapter = CheckListAdapter(checklists,  { deleteChecklist(it) })
            }
        )
    }

    private fun deleteComment(comment: Comment) {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.deleteComment(card.id, comment.id, token)
            },
            success = {
                val position = comments.indexOf(comment)
                comments.remove(comment)
                commentRecycler.adapter?.notifyItemRemoved(position)
            }
        )
    }

    private fun updateComment(comment: Comment) {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.updateComment(card.id, comment.id, comment.data.text, token)
            },
            success = {}
        )
    }

    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_labels, null)
        val labelAlertDialogRecycler = dialogView.findViewById<RecyclerView>(R.id.labelRecycler)
        labelAlertDialogRecycler.adapter = LabelAdapter(boardLabels, card, { addLabel(it) }, { deleteLabel(it) })
        with(builder) {
            setView(dialogView)
            setTitle(R.string.labels)
            setPositiveButton(android.R.string.yes) { dialog, which ->
                Toast.makeText(applicationContext,
                    android.R.string.yes, Toast.LENGTH_SHORT).show()
            }
            show()
        }
    }

    private fun addLabel(label: Label) {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                val labelId = label.id
                webService.addLabel(card.id, labelId, token)
            },
            success = {
            }
        )
    }

    private fun deleteLabel(label: Label) {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                val labelId = label.id
                webService.deleteLabel(card.id, labelId, token)
            },
            success = {
            }
        )
    }

    private fun deleteChecklist(checkList: CheckList) {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.deleteChecklist(card.id, checkList.id , token)
            },
            success = {}
        )
    }
}