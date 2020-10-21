package ru.irina.trello_plus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        val token = getSP().getString(SP_LOGIN, "")!!
        val tokenBlank = token.isBlank()

        if(tokenBlank) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}