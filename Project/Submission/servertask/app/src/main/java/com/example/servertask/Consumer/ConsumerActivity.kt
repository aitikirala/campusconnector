package com.example.servertask.Consumer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.R
import com.example.servertask.Start.LoginActivity
import com.example.servertask.ThreadTasks.ThreadTaskPostSignUp
import com.example.servertask.UpdateViewInterface

class ConsumerActivity : AppCompatActivity(), UpdateViewInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumer)

        val addButton: Button = findViewById(R.id.addNameButton)
        addButton.setOnClickListener {
            addName()
        }

    }

    private fun addName() {
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val enteredName = nameEditText.text.toString().trim()

        val ageEditText: EditText = findViewById(R.id.ageEditText)
        val enteredAge = ageEditText.text.toString().trim()

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val enteredEmail = emailEditText.text.toString().trim()

        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val enteredUsername = usernameEditText.text.toString().trim()

        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val enteredPassword = passwordEditText.text.toString().trim()

        // Validate username
        if (enteredUsername.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate password
        if (enteredPassword.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
            return
        }


        // Validate name
        if (enteredName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate age
        if (enteredAge.isEmpty() || !enteredAge.matches("\\d+".toRegex())) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate email
        if (enteredEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        Log.w("MA", "Creating ThreadTask object")
        val task: ThreadTaskPostSignUp = ThreadTaskPostSignUp(this)

        val postParams = HashMap<String, String>()
        postParams["name"] = enteredName
        postParams["age"] = enteredAge
        postParams["email"] = enteredEmail
        postParams["username"] = enteredUsername
        postParams["password"] = enteredPassword
        task.setPostData(postParams)

        Log.w("MA", "Start thread")
        task.start()
        Log.w("MA", "Inside addName, Thread started")

        // Clear the name and age EditText fields after adding the entry
        nameEditText.text.clear()
        ageEditText.text.clear()
        emailEditText.text.clear()
        usernameEditText.text.clear()
        passwordEditText.text.clear()



        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }

    override fun updateView(s: String) {
        Log.w("MA", "Inside updateView, s is $s")
        val tv: TextView = findViewById(R.id.tv)
        tv.text = s

        
    }
    override fun runOnUi(runnable: Runnable) {
        runOnUiThread(runnable)
    }


    companion object{

        var address: String = "null"
        var BusinessReceivedOrders: List<String> = listOf()
        var ConsumersSentOrders: List<String> = listOf()
        var businessUsername: String = "null"
        var averageRating: Double? = 0.0
        var businessId: String = "null"
        var consumerUsername: String =  "null"
        var consumerid: String =  "null"

        var URL_PHP_GET : String  = "https://cmsc436-2301.cs.umd.edu/testGet.php?name=Aarti&age=21"
        var URL_PHP_POST_CONSUMER : String  = "https://499aitikira.cs.umd.edu/cgi-bin/add_name.php"
        var URL_PHP_POST_BUSINESS : String  = "https://499aitikira.cs.umd.edu/cgi-bin/addBusiness.php"
        var isBusinessSelected: Boolean = false
        var URL_JSON : String = "https://cmsc436-2301.cs.umd.edu/json.php"
        var URL_PHP_GET_BUSINESS : String  = "https://499aitikira.cs.umd.edu/cgi-bin/searchBus.php"
        var URL_PHP_VALIDATE: String = "https://499aitikira.cs.umd.edu/cgi-bin/validate.php"
    }

    /*Look into ListView: present in list form
     */
}