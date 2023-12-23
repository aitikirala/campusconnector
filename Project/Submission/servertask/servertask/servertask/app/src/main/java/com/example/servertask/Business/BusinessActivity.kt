package com.example.servertask.Business

import android.content.Intent
import android.widget.Toast
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.servertask.R
import com.example.servertask.Start.LoginActivity
import com.example.servertask.ThreadTasks.ThreadTaskPostSignUp
import com.example.servertask.UpdateViewInterface

class BusinessActivity : AppCompatActivity(), UpdateViewInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)


        val addButton: Button = findViewById(R.id.addNameButton)
        addButton.setOnClickListener {
            addBusiness()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun addBusiness(){
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val enteredName = nameEditText.text.toString().trim()

        val phoneEditText: EditText = findViewById(R.id.phoneEditText)
        val enteredPhone = phoneEditText.text.toString().trim()

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val enteredEmail = emailEditText.text.toString().trim()

        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val enteredusername = usernameEditText.text.toString().trim()

        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val enteredpassword = passwordEditText.text.toString().trim()

        val AddressEditText: EditText = findViewById(R.id.AddressEditText)
        val enteredAddress = AddressEditText.text.toString().trim()


        // Validate name
        if (enteredName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate phone
        if (enteredPhone.isEmpty() || !enteredPhone.matches("\\d+".toRegex())) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate email
        if (enteredEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate address
        if (enteredAddress.isEmpty()) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show()
            return
        }


        Log.w("MA", "Creating ThreadTask object")
        val task: ThreadTaskPostSignUp = ThreadTaskPostSignUp(this)
        val postParams = HashMap<String, String>()
        postParams["name"] = enteredName
        postParams["email"] = enteredEmail
        postParams["phone_number"] = enteredPhone
        postParams["username"] = enteredusername
        postParams["password"] = enteredpassword
        postParams["Address"] = enteredAddress
        task.setPostData(postParams)

        Log.w("MA", "Start thread")
        task.start()
        Log.w("MA", "Inside addName, Thread started")

        // Clear the name and age EditText fields after adding the entry
        nameEditText.text.clear()
        phoneEditText.text.clear()
        emailEditText.text.clear()
        usernameEditText.text.clear()
        passwordEditText.text.clear()
        AddressEditText.text.clear()
    }

    override fun updateView(s: String) {
        Log.w("MA", "Inside updateView, s is $s")
        val tv: TextView = findViewById(R.id.tv)
        tv.text = s
    }

    override fun runOnUi(runnable: Runnable) {
        runOnUiThread(runnable)
    }



}
