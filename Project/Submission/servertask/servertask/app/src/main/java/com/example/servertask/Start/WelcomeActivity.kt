package com.example.servertask.Start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.servertask.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)

        btnLogin.setOnClickListener {
            // Navigate to login screen (assuming you have a LoginActivity)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            // Navigate to sign up screen (assuming you have a SignUpActivity)
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
