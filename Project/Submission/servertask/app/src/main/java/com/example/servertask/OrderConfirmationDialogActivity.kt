package com.example.servertask

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmationDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation_dialog)

        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
// ... set up your dialog ...
        builder.setTitle("Order Confirmation") // Make sure this line has the correct title
// ... other dialog setup code ...

        // Set the message
        val messageTextView: TextView = findViewById(R.id.messageTextView)
        messageTextView.text = "Your order has been confirmed"

        // Close the activity after 5 seconds (5000 milliseconds)
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 5000)
    }
}


