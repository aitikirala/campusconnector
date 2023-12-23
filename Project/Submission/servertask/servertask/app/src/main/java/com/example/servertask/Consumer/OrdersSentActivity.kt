package com.example.servertask.Consumer

import ThreadTaskConsumerId
import android.R.attr.button
import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.R
import com.example.servertask.ThreadTasks.ThreadTaskCancelOrder
import com.example.servertask.ThreadTasks.ThreadTaskShowOrders
import com.example.servertask.UpdateViewInterface
import com.google.android.material.bottomnavigation.BottomNavigationView


class OrdersSentActivity: AppCompatActivity(), UpdateViewInterface {

    private lateinit var layoutOrders: LinearLayout
    private var isFetchingBusId = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_sent)

        layoutOrders = findViewById(R.id.layout_orders)

        val display: TextView = findViewById(R.id.Display)
        display.text = "Here are your orders!"

        isFetchingBusId = true


        ThreadTaskConsumerId { consumerId ->
            runOnUiThread {
                Log.d("ConsumerIdFetcher", "Consumer ID: $consumerId")
                ConsumerActivity.consumerid = consumerId;
                getOrders(consumerId)

            }
        }.execute()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    val intent = Intent(this, BusinessSearch::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_favorites -> {
                    val intent = Intent(this, ListFavActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ConsumerProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_orders -> {
                    val intent = Intent(this, OrdersSentActivity::class.java)
                    startActivity(intent)
                    true
                }
                // Handle other item clicks if necessary
                else -> false
            }
        }


    }

    override fun updateView(result: String) {
        ConsumerActivity.ConsumersSentOrders = result.split("|").filter { it.isNotEmpty() }
        createOrderButtons(ConsumerActivity.ConsumersSentOrders)

    }

    override fun runOnUi(runnable: Runnable) {
        this@OrdersSentActivity.runOnUiThread(runnable)
    }

    private fun getOrders(consumerId: String) {
        val task = ThreadTaskShowOrders(this)
        task.setPostData(consumerId)
        task.start()
    }

    private fun createOrderButtons(orders: List<String>) {
        layoutOrders.removeAllViews() // Clear existing views

        orders.forEach { order ->
            val button = Button(this)
            button.text = order
            button.setOnClickListener {
                // Handle button click
                showCancelOrderDialog(order)
            }
            layoutOrders.addView(button)
        }
    }

    private fun showCancelOrderDialog(order: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setMessage("Would you like to cancel this order?")
            .setPositiveButton("Yes") { _, _ ->
                cancelOrder(order)

            }
            .setNegativeButton("No") { _, _ ->
                // User clicked "No," you can close the dialog or perform
                // any other action here.
            }
            .create()
            .show()
    }

    private fun cancelOrder(order: String) {
        val task = ThreadTaskCancelOrder(this)
        task.setPostData(ConsumerActivity.consumerid, order)
        task.run()
    }



}