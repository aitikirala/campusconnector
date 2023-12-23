package com.example.servertask.Consumer

import ThreadTaskBusinessId
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
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
                // Text has changed, apply any filter logic if needed

                filterAndUpdateViewWithJson(newText)
                return false
            }
        })


    }

    private fun createBusinessCardView(businessName: String): CardView {
        return CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // Set consistent margin around the cards
                val margin = 16.dpToPx(resources)
                setMargins(margin, margin, margin, margin)
            }
            radius = 16.dpToPx(resources).toFloat() // Increased corner radius for rounded corners
            cardElevation = 8.dpToPx(resources).toFloat() // Increased elevation for shadow

            setCardBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))

            val textView = TextView(this@BusinessSearch).apply {
                text = businessName
                setTextColor(ContextCompat.getColor(context, R.color.white))
                textSize = 16f
                // Set padding inside the card for the TextView
                val padding = 16.dpToPx(resources)
                setPadding(padding, padding, padding, padding)
            }

            addView(textView)

            setOnClickListener {
                // Handle the card click
                retrieveAndDisplayBusinessInfo(businessName)
            }
        }
    }



    private fun Int.dpToPx(resources: Resources): Int {
        return (this * resources.displayMetrics.density).toInt()
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
        val businessNames = businessNamesString.split("\n")

        llBusinessContainer.removeAllViews()

        for (businessName in businessNames) {
            val cardView = createBusinessCardView(businessName)
            llBusinessContainer.addView(cardView)
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