package com.example.servertask.Consumer

import ThreadTaskBusinessId
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout

import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.R

import com.example.servertask.ThreadTasks.ThreadTaskSearchBusinesses
import com.example.servertask.UpdateViewInterface
import com.google.android.material.bottomnavigation.BottomNavigationView

class BusinessSearch : AppCompatActivity(), UpdateViewInterface {

    private lateinit var etSearchBar: SearchView // Change the type to SearchView
    private lateinit var btnSearch: Button // Define the button

    private lateinit var llBusinessContainer: LinearLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_search)

        llBusinessContainer = findViewById(R.id.llBusinessContainer)
        etSearchBar = findViewById(R.id.etSearchBar)

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

        etSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // User pressed the search key
                filterAndUpdateViewWithJson(query)
                return true // Handle the search key press
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterAndUpdateViewWithJson(newText)
                return false
            }
        })


    }


    fun filterAndUpdateViewWithJson(searchQuery: String) {
        // Start the thread to get the business names and filter based on the search query
        val task = ThreadTaskSearchBusinesses(this, searchQuery)
        task.start()
    }


    override fun updateView(s: String) {
        updateViewWithJson(s)
    }

    override fun runOnUi(runnable: Runnable) {
        runOnUiThread(runnable)
    }

    fun updateViewWithJson(businessNamesString: String) {
        Log.w("MainActivity", "Received business names string: $businessNamesString")
        val businessNames = businessNamesString.split("\n")

        // Clear previous buttons
        llBusinessContainer.removeAllViews()

        for (businessName in businessNames) {
            val button = Button(this)
            button.text = businessName
            button.setOnClickListener {
                // Handle the button click
                retrieveAndDisplayBusinessInfo(businessName)
            }
            llBusinessContainer.addView(button)
        }
    }

    private fun retrieveAndDisplayBusinessInfo(businessName: String) {
        ThreadTaskBusinessId(businessName) { businessId ->
            runOnUiThread {
                val intent = Intent(this@BusinessSearch, BusinessDetailActivity::class.java)
                intent.putExtra("business_name", businessName)
                intent.putExtra("business_id", businessId)

                startActivity(intent)
            }
        }.execute()
    }


}