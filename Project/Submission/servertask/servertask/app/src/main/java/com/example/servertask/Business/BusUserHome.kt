package com.example.servertask.Business

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BusUserHome : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_userhome)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_profile -> {
                    val intent = Intent(this, BusinessProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_orders -> {
                    val intent = Intent(this, OrdersReceivedActivity::class.java)
                    startActivity(intent)
                    true
                }
                // Handle other item clicks if necessary
                else -> false
            }
        }
    }
}