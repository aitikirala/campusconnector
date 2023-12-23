package com.example.servertask.Business

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.Consumer.ConsumerActivity
import com.example.servertask.R
import com.example.servertask.ThreadTasks.ThreadTaskGetBusIdByUsername
import com.example.servertask.ThreadTasks.ThreadTaskGetOrders
import com.example.servertask.UpdateViewInterface

class OrdersReceivedActivity: AppCompatActivity(), UpdateViewInterface {

    private lateinit var Page: TextView
    private lateinit var layoutOrders: LinearLayout
    private var isFetchingBusId = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_received)

        Page= findViewById(R.id.Page)

        layoutOrders = findViewById(R.id.layout_orders)

        isFetchingBusId = true

        val task = ThreadTaskGetBusIdByUsername(this)
        task.setPostData(ConsumerActivity.businessUsername)
        task.start()

    }


    override fun updateView(result: String) {
        if (isFetchingBusId) {
            isFetchingBusId = false
            ConsumerActivity.businessId = result.trim()
            getOrders()
        } else {
            ConsumerActivity.BusinessReceivedOrders = result.split("|").filter { it.isNotEmpty() }
            createOrderButtons(ConsumerActivity.BusinessReceivedOrders)
        }
    }

    private fun getOrders() {
        val task = ThreadTaskGetOrders(this)
        task.setPostData(ConsumerActivity.businessId)
        task.start()
    }

    private fun createOrderButtons(orders: List<String>) {
        layoutOrders.removeAllViews()

        orders.forEach { order ->
            val button = Button(this)
            button.text = order
            Log.d ("Order", "${order}")
            button.setOnClickListener {
                // Handle button click
                Toast.makeText(this, "Order: $order", Toast.LENGTH_SHORT).show()
            }
            layoutOrders.addView(button)
        }
    }

    override fun runOnUi(runnable: Runnable) {
        this@OrdersReceivedActivity.runOnUiThread(runnable)
    }

}
