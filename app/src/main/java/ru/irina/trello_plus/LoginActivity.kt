package ru.irina.trello_plus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private companion object {
        const val LOGIN_URL = "https://trello.com/1/authorize?expiration=never&name=TrelloPlus&scope=read,write,account&response_type=token&key=$API_KEY"
    }

    private var grantAccessButtonClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        action.setOnClickListener {
            onActionClick()
        }
    }

    private fun onActionClick() {
        if(grantAccessButtonClicked)
            onLoginClick()
        else
            onGrantAccessClick()
    }

    private fun onGrantAccessClick() {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(LOGIN_URL))
        startActivity(i)

        token.isEnabled = true
        action.setText(R.string.login)

        grantAccessButtonClicked = true
    }

    private fun onLoginClick() {
        getSPE().putString(SP_LOGIN, token.text).apply()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}