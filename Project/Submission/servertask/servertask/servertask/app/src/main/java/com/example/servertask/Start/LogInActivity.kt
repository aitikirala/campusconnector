package com.example.servertask.Start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.Business.BusUserHome
import com.example.servertask.Consumer.ConsumerActivity
import com.example.servertask.R
import com.example.servertask.ThreadTasks.ThreadTaskPostLogin
import com.example.servertask.UpdateViewInterface
import com.example.servertask.Consumer.ConsUserHome

class LoginActivity : AppCompatActivity(), UpdateViewInterface {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLoginSubmit: Button
    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit)

        btnLoginSubmit.setOnClickListener {
            attemptLogin()
        }

        etPassword.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.action == KeyEvent.ACTION_UP) {
                attemptLogin()
            }
            true
        }

    }

    private fun attemptLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Make sure username and password are not empty before attempting to log in
        if (username.isNotEmpty() && password.isNotEmpty()) {
            validate(username, password)
            Log.d("LoginActivity", "Username: $username, Password: $password")
        } else {
            Toast.makeText(this, "Username/Password should not be empty", Toast.LENGTH_SHORT).show()
        }
    }



    private fun validate(username: String, password: String){

        val task = ThreadTaskPostLogin(this)

        val postParams = HashMap<String, String>()
        postParams["username"] = username
        postParams["password"] = password

        user = username

        task.setPostData(postParams)

        Log.w("MA", "Start thread")
        task.start()

    }

    override fun updateView(s: String) {
        // Assuming "success" indicates a successful login
        Log.d("Message",  "$s");
        if ("success0" == s.trim()) {
            Log.d("It is a", "Business")
            ConsumerActivity.businessUsername = user;
            runOnUiThread {
                val intent = Intent(this, BusUserHome::class.java)
                startActivity(intent)
            }
        } else if ("success1" == s.trim()) {
            Log.d("It is a", "Consumer")
            ConsumerActivity.consumerUsername = user;
            runOnUiThread {
                val intent = Intent(this, ConsUserHome::class.java)
                startActivity(intent)
            }
        } else {
            val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
            etPassword.startAnimation(shake)
            // Optionally, show an error message
            etPassword.error = "Password Incorrect"
        }
    }

    override fun runOnUi(runnable: Runnable) {
        runOnUiThread(runnable)
    }

}
