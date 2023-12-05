package com.example.servertask.Consumer

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.OrderConfirmationDialogActivity
import com.example.servertask.R
import com.example.servertask.ThreadTasks.ThreadTaskGetServices
import com.example.servertask.ThreadTasks.ThreadTaskMakeOrder
import com.example.servertask.UpdateViewInterface
import com.google.android.material.bottomnavigation.BottomNavigationView

class ViewServicesActivity: AppCompatActivity(), UpdateViewInterface {

    private lateinit var mainTextView: TextView
    private lateinit var submit: Button
    private val checkBoxes = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_services)

        mainTextView = findViewById(R.id.Page)
        getServices() // Fetch services when activity starts

        submit = findViewById(R.id.btnSubmit)  // Replace with your button ID
        submit.setOnClickListener {
            val checkedServices = checkBoxes.filter { it.isChecked }.map { it.text.toString() }
            // Now checkedServices contains the names of all checked services
            // You can use this list as needed

            Log.d("ViewServicesActivity", "Checked services: $checkedServices")
            placeOrder(checkedServices)

            showPopupMessage()


        }

    }

    private fun showPopupMessage() {
        // Start the OrdersSentActivity
        val intent = Intent(this, OrdersSentActivity::class.java)
        startActivity(intent)

        // Start the dialog-themed activity
        val dialogIntent = Intent(this, OrderConfirmationDialogActivity::class.java)
        startActivity(dialogIntent)
    }



    private fun placeOrder(checkedServices: List<String>) {
        val concatenatedString: String = checkedServices.joinToString(separator = ", ")
        val task = ThreadTaskMakeOrder(this)
        task.setPostData(
            ConsumerActivity.consumerid,
            ConsumerActivity.businessId, concatenatedString)
        task.start()
    }

    private fun getServices() {
        val task = ThreadTaskGetServices(this)
        task.setPostData(ConsumerActivity.businessId)
        task.start()
    }

    private fun createServiceCheckboxes(serviceList: List<String>) {
        val linearLayout: LinearLayout = findViewById(R.id.linear_layout_services)
        linearLayout.removeAllViews() // Clear previous views
        checkBoxes.clear() // Clear existing CheckBox references


        serviceList.forEach { serviceName ->
            val checkBox = CheckBox(this)
            checkBox.text = serviceName
            Log.d("checkbox: ", "${serviceName}")
            checkBoxes.add(checkBox)
            linearLayout.addView(checkBox)
        }
    }



    override fun updateView(result: String) {
        // Split the result string by comma and trim each part to remove leading/trailing whitespaces and newlines
        val serviceList = result.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        Log.d("ProcessedServices", serviceList.toString())
        createServiceCheckboxes(serviceList)
    }


    override fun runOnUi(runnable: Runnable) {
        this@ViewServicesActivity.runOnUiThread(runnable)
    }
}
