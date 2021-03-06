package ru.irina.trello_plus

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_card.*
import ru.irina.trello_plus.WebService.Companion.makeSafeApiCall
import java.text.SimpleDateFormat
import java.util.*

class CardActivity : AppCompatActivity(), CardProcessor {

    private val webService = WebService.build()
    private lateinit var card: Card
    private lateinit var cardPopup: PopupMenu
    private var comments = mutableListOf<Comment>()
    private var boardMembers = listOf<Member>()
    private var checklists = mutableListOf<CheckList>()
    private var boardLabels = listOf<Label>()
    private var cardDate = ""
    private var cardTime = ""
    private var cardDueDate: Date? = null
    private lateinit var checkListAdapter: CheckListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        getCardData()
        setUpViews()
    }

    private fun getCardData() {
        card = intent.getParcelableExtra("card")!!
        boardMembers = intent.getParcelableArrayListExtra("boardMembers")!!

        boardMembers.filter { card.idMembers.contains(it.id) }.forEach { it.checked = true }

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
        postComment.setOnClickListener {
            addComment()
        }
        getComments()
        getMembers()
        setDate()
        setUpDateCheckBox()
        getChecklists()
        addChecklistBg.setOnClickListener {
            createNewChecklist()
        }
        labelsBg.setOnClickListener {
            createLabelAlertDialog()
        }
        dateBg.setOnClickListener {
            createDateAlertDialog()
        }
        membersBg.setOnClickListener {
            createMemberAlertDialog()
        }
    }

    private fun updateCardRequest() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.updateCard(
                    card.id,
                    cardTitle.text.toString(),
                    description.text.toString(),
                    token
                )
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
                commentRecycler.adapter = CommentAdapter(comments, { deleteComment(it) }, {
                    updateComment(it) })
            }
        )
    }

    private fun addComment() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.addComment(card.id, commentText.text.toString(), token)
            },
            success = {
                commentText.setText("")
                toast(R.string.saved)
            }
        )
    }

    private fun getMembers() {
        when (card.idMembers.size) {
            1 -> {
                firstMember.visibility = View.VISIBLE
                otherMember.visibility = View.VISIBLE
                firstMember.text = boardMembers.first { it.id == card.idMembers[0] }.initials
            }
            2 -> {
                firstMember.visibility = View.VISIBLE
                secondMember.visibility = View.VISIBLE
                otherMember.visibility = View.VISIBLE
                firstMember.text = boardMembers.first { it.id == card.idMembers[0] }.initials
                secondMember.text = boardMembers.first { it.id == card.idMembers[1] }.initials
            }
            3 -> {
                firstMember.visibility = View.VISIBLE
                secondMember.visibility = View.VISIBLE
                thirdMember.visibility = View.VISIBLE
                otherMember.visibility = View.VISIBLE
                firstMember.text = boardMembers.first { it.id == card.idMembers[0] }.initials
                secondMember.text = boardMembers.first { it.id == card.idMembers[1] }.initials
                thirdMember.text = boardMembers.first { it.id == card.idMembers[2] }.initials
            }
            else ->
                membersText.visibility = View.VISIBLE
        }
    }
    
    private fun setDate() {
        val dateAndTime = setDate(
            this,
            card,
            checkBox,
            cardDueTime,
            calendarIcon,
            getString(R.string.date_of_completion)
        )
        if(dateAndTime.first.isNotBlank() && dateAndTime.second.isNotBlank()) {
            cardDate = dateAndTime.first
            cardTime = dateAndTime.second
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
                success = {
                    if(checkBox.isChecked) {
                        cardDueTime.setTextColor(resources.getColor(R.color.green))
                        calendarIcon.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.green))
                    } else updateDateColors(this, card, cardDueTime, calendarIcon)
                }
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
                checklists = it as MutableList<CheckList>

                checkListAdapter = CheckListAdapter(checklists, { deleteChecklist(it) },
                    { updateCheckItem(it) }, { addCheckItem(it) }, { deleteCheckItem(it) })
                checkListRecycler.adapter = checkListAdapter
            }
        )
    }

    private fun createNewChecklist() {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.createNewChecklist(card.id, getString(R.string.Checklist),token)
            },
            success = {
                checklists.add(it)
                val checkListIndex = checklists.indexOf(it)
                checkListAdapter.notifyItemInserted(checkListIndex)
            }
        )
    }

    private fun updateCheckItem(item: CheckList.Item) {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                val itemId = item.id
                webService.updateCheckItem(card.id, itemId, item.name, item.state, token)
            },
            success = {
            }
        )
    }

    private fun addCheckItem(checkList: CheckList) {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.addCheckItem(checkList.id, checkList.addItem, token)
            },
            success = { checkListItem ->
                checkList.checkItems.add(checkListItem)
                val hidden = getSP().getBoolean("${checkListItem.idChecklist}$CHECKLIST_HIDE_DONE_STATE", false)
                val newItems = if(hidden) checkList.checkItems.filter { it -> !it.complete  } else checkList.checkItems
                val itemAdapter = checkListAdapter.getCheckItemAdapter(checklists.indexOf(checkList))
                itemAdapter.submitList(newItems)
            })
    }

    private fun deleteCheckItem(item: CheckList.Item) {
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.deleteCheckItem(item.idChecklist, item.id, token)
            },
            success = {
                val checkList = checklists.find { it.id == item.idChecklist }
                val checkListIndex = checklists.indexOf(checkList)
                checkList?.let {
                    val checkItemIndex = it.checkItems.indexOf(item)
                    val hidden = getSP().getBoolean("${it.id}$CHECKLIST_HIDE_DONE_STATE", false)
                    val visibleItems = it.checkItems.filter { !it.complete }
                    val checkItemVisualIndex = if(hidden) visibleItems.indexOf(item) else checkItemIndex
                    it.checkItems.removeAt(checkItemVisualIndex)
                    val newItems = if(hidden) checkList.checkItems.filter { !it.complete  } else checkList.checkItems
                    checkListAdapter.getCheckItemAdapter(checkListIndex).submitList(newItems)
                }
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

    private fun createLabelAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_labels, null)
        val labelAlertDialogRecycler = dialogView.findViewById<RecyclerView>(R.id.labelRecycler)
        labelAlertDialogRecycler.adapter = LabelAdapter(boardLabels, card, { addLabel(it) }, {
            deleteLabel(
                it
            )
        })
        with(builder) {
            setView(dialogView)
            setTitle(R.string.labels)
            setPositiveButton(android.R.string.yes) { dialog, which ->
                Toast.makeText(
                    applicationContext,
                    android.R.string.yes, Toast.LENGTH_SHORT
                ).show()
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
                webService.deleteChecklist(card.id, checkList.id, token)
            },
            success = {
                checklists.remove(checkList)
                checkListAdapter.notifyItemRemoved(checklists.indexOf(checkList))
            }
        )
    }

    private fun createDateAlertDialog() {
        val builder = AlertDialog.Builder(this)
        Toast.makeText(
            applicationContext,
            android.R.string.dialog_alert_title, Toast.LENGTH_SHORT)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_date_time, null)
        val dateLayout = dialogView.findViewById<LinearLayout>(R.id.dateLayout)
        val timeLayout = dialogView.findViewById<LinearLayout>(R.id.timeLayout)
        val dateText = dialogView.findViewById<TextView>(R.id.dateText)
        val timeText = dialogView.findViewById<TextView>(R.id.timeText)
        var dialogCardDate = if(cardDate.isNotBlank()) cardDate else getString(R.string.date)
        var dialogCardTime = if(cardTime.isNotBlank()) cardTime else getString(R.string.time)

        val c = Calendar.getInstance()
        getCardDueDate(card)?.let {
            c.time = it
        }

        with(builder) {
            setView(dialogView)
            setTitle(R.string.due_date)
            setPositiveButton(android.R.string.yes) { dialog, which ->
                    if(dateText.text != getString(R.string.date) && timeText.text != getString(R.string.time)) {
                        cardDueTime.text = "${dateText.text} ${getString(R.string.at)} ${timeText.text}"
                        cardDate = dialogCardDate
                        cardTime = dialogCardTime

                        val formattedDueDate = SimpleDateFormat(
                            DATE_PARSING_PATTERN,
                            Locale.getDefault()
                        ).format(c.time)
                        Log.i("CARD","$formattedDueDate")
                        card.badges.due = formattedDueDate
                        updateCardDue(formattedDueDate)

                        updateDateColors(this@CardActivity, card, cardDueTime, calendarIcon)
                    }
            }
            setNegativeButton(R.string.cancel) { _, _ ->}
            if(cardDate.isNotBlank() && cardTime.isNotBlank())
                setNeutralButton(R.string.delete) { dialogInterface, _ ->
                    updateCardDue("")
                    cardDate = ""
                    cardTime = ""
                    card.badges.due = ""
                    cardDueTime.text = getString(R.string.date_of_completion)
                    cardDueTime.setTextColor(resources.getColor(R.color.black))
                    calendarIcon.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.black))
                }
            show()
        }

        dateText.text = dialogCardDate
        timeText.text = dialogCardTime

        if(cardDueDate != null)
            c.time = cardDueDate!!

        dateLayout.setOnClickListener {
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                { datePicker, dateYear, monthOfYear, dayOfMonth ->
                    c.set(dateYear, monthOfYear, dayOfMonth)
                    dialogCardDate =
                        SimpleDateFormat(DATE_FORMATTING_PATTERN, Locale.getDefault()).format(
                            c.time
                        )
                    dateText.text = dialogCardDate
                },
                year, month, day
            )
            dpd.show()
        }
        timeLayout.setOnClickListener {
            val hh = c.get(Calendar.HOUR_OF_DAY)
            val mm = c.get(Calendar.MINUTE)
            val tpd = TimePickerDialog(
                this, TimePickerDialog.OnTimeSetListener
                { view, hourOfDay, minute ->
                    dialogCardTime = "${hourOfDay.twoDigitString}:${minute.twoDigitString}"
                    timeText.text = dialogCardTime
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    c.set(Calendar.MINUTE, minute)
                    c.set(Calendar.SECOND, 0)
                }, hh, mm, true
            )
            tpd.show()
        }
    }

    private fun updateCardDue(due: String) {
            makeSafeApiCall(
                request = {
                    val token = getSP().getString(SP_LOGIN, "")!!
                    webService.updateCardDue(card.id, due, token)
                },
                success = {
                    checkBox.visibility = if(due.isEmpty()) View.GONE else View.VISIBLE
                }
            )
    }

    private fun addMember(member: Member) {
        val memberId = member.id
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.addMember(card.id, memberId, token)
            },
            success = {
                card.idMembers.add(member.id)
            }
        )
    }

    private fun deleteMember(member: Member) {
        val memberId = member.id
        makeSafeApiCall(
            request = {
                val token = getSP().getString(SP_LOGIN, "")!!
                webService.deleteMember(card.id, memberId, token)
            },
            success = {
                card.idMembers.remove(member.id)
            }
        )
    }

    private fun createMemberAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_members, null)
        val memberAlertDialogRecycler = dialogView.findViewById<RecyclerView>(R.id.memberRecycler)
        memberAlertDialogRecycler.adapter = MemberAdapter(boardMembers)
        with(builder) {
            setView(dialogView)
            setTitle(R.string.card_members)
            setPositiveButton(android.R.string.yes) { dialog, which ->
                updateRequestMember()
            }
            show()
        }
    }

    private fun updateRequestMember() {
        boardMembers.forEach {
            when {
                card.idMembers.contains(it.id) && !it.checked -> deleteMember(it)
                !card.idMembers.contains(it.id) && it.checked -> addMember(it)
            }
        }
    }
}