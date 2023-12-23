package com.example.servertask.Consumer

import ThreadTaskAverageRating
import ThreadTaskConsumerId
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.FavoriteTaskCallback
import com.example.servertask.R
import com.example.servertask.ThreadTasks.ThreadTaskFavorite
import com.example.servertask.ThreadTasks.ThreadTaskGetAddress
import com.example.servertask.ThreadTasks.ThreadTaskGetDescription
import com.example.servertask.UpdateViewInterface
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.function.Consumer

class BusinessDetailActivity : AppCompatActivity(), UpdateViewInterface, FavoriteTaskCallback {
    private lateinit var btnRate: Button
    private lateinit var btnFavorite: Button
    private lateinit var btnServices: Button
    private lateinit var Description: TextView
    private lateinit var tvAverageRating: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_detail)

        btnRate = findViewById(R.id.btnRate)
        btnFavorite = findViewById(R.id.btnFavorite)
        btnServices = findViewById(R.id.btnServices)
        Description = findViewById(R.id.BusinessDesc)
        tvAverageRating = findViewById(R.id.tvAverageRating)

        // Get the business name from the intent
        val businessName = intent.getStringExtra("business_name")
        ConsumerActivity.businessId = intent.getStringExtra("business_id").toString()


        // Set the business name to the TextView
        val tvBusinessName: TextView = findViewById(R.id.tvBusinessName)
        tvBusinessName.text = "${businessName}"

        getAverage(ConsumerActivity.businessId)

        val task = ThreadTaskGetDescription(this)
        if (businessName != null) {
            task.setPostData(businessName)
        }
        task.start()

        btnRate.setOnClickListener {
            val intent = Intent(this, RatingActivity::class.java)
            startActivity(intent)
        }

        btnFavorite.setOnClickListener {
            val task = ThreadTaskFavorite(this)
            task.setPostData(ConsumerActivity.consumerid, ConsumerActivity.businessId)
            task.start()
        }

        btnServices.setOnClickListener {
            val intent = Intent(this, ViewServicesActivity::class.java)
            startActivity(intent)
        }

        val task2 = ThreadTaskGetAddress(this)
        if (businessName != null) {
            task2.setPostData(businessName)
        }
        task2.start()

        val openMapsButton: Button = findViewById(R.id.openMapsButton)
        openMapsButton.setOnClickListener {

            val encodedAddress = Uri.encode(ConsumerActivity.address)

            // Create the intent to open Google Maps at the specified address
            val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedAddress")
            //val gmmIntentUri = Uri.parse("geo:0,0?q=restaurants") // This will search for restaurants near the user's current location

            // Create an Intent to open Google Maps
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Ensure Google Maps app handles the intent

            // Attempt to start an activity that can handle the Intent
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // Google Maps app is not installed
                Toast.makeText(this, "Google Maps app is not installed.", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun getAverage(businessId: String) {
        val task = ThreadTaskAverageRating(businessId) { averageRating ->
            runOnUiThread {
                tvAverageRating.text = "Average Rating: $averageRating"
            }
        }
        task.start()
    }

    override fun onResume() {
        super.onResume()
        // Update the TextView every time the activity resumes
        tvAverageRating.text = "Average Rating: ${ConsumerActivity.averageRating}"
    }

    private fun showFavoriteAddedNotification() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Favorites")
        builder.setMessage("This item has been added to your favorites.")
        builder.setPositiveButton("OK") { dialog, which ->
            // Handle the OK button click
        }
        // Optionally, you can add a negative button
        // builder.setNegativeButton(android.R.string.no) { dialog, which -> ... }

        builder.show()
    }

    private fun showFavoriteRemovedNotification() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Favorites")
        builder.setMessage("This item has been removed from your favorites.")
        builder.setPositiveButton("OK") { dialog, which ->
            // Handle the OK button click
        }
        // Optionally, you can add a negative button
        // builder.setNegativeButton(android.R.string.no) { dialog, which -> ... }

        builder.show()
    }

    override fun updateView(s: String) {
        ConsumerActivity.consumerUsername?.let {
            fetchAndStoreConsumerId()
        }

        Description.text = s

    }

    override fun onFavoriteTaskCompleted(result: String) {
        runOnUiThread {
            if (result == "New record created successfully") {
                showFavoriteAddedNotification()
            } else if (result == "Record deleted successfully")
                showFavoriteRemovedNotification()
        }
    }

    private fun fetchAndStoreConsumerId() {
        ThreadTaskConsumerId { consumerId ->
            runOnUiThread {
                Log.d("ConsumerIdFetcher", "Consumer ID: $consumerId")
                ConsumerActivity.consumerid = consumerId;

            }
        }.execute()
    }


    override fun runOnUi(runnable: Runnable) {
        runOnUiThread(runnable)
    }
}
