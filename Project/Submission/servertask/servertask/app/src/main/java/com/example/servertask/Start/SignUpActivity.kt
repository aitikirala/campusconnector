package com.example.servertask.Start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.servertask.Business.BusinessActivity
import com.example.servertask.Consumer.ConsumerActivity
import com.example.servertask.Consumer.ConsumerActivity.Companion.isBusinessSelected
import com.example.servertask.R

class SignUpActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnBackToMain: Button = findViewById(R.id.btnBackToMain)
        val btnAnotherAction: Button = findViewById(R.id.btnAnotherAction)

        btnBackToMain.setOnClickListener {
            isBusinessSelected = false
            val intent = Intent(this, ConsumerActivity::class.java)
            startActivity(intent)
        }

        btnAnotherAction.setOnClickListener {
            isBusinessSelected = true
            val intent = Intent(this, BusinessActivity::class.java)
            startActivity(intent)
        }

    }
}
