package com.example.servertask.Consumer

import ThreadTaskAverageRating
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.example.servertask.R
import com.example.servertask.ThreadTasks.ThreadTaskGetCardsForReviews
import com.example.servertask.ThreadTasks.ThreadTaskPostRating
import com.example.servertask.ThreadTasks.ThreadTaskPostReview
import com.example.servertask.UpdateViewInterface

class RatingActivity : AppCompatActivity(), UpdateViewInterface {

    private lateinit var ratingBar: RatingBar
    private lateinit var btnSubmitRating: Button
    private lateinit var tvRating: TextView
    private lateinit var etReview: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        ratingBar = findViewById(R.id.ratingBar)
        btnSubmitRating = findViewById(R.id.btnSubmitRating)
        tvRating = findViewById(R.id.tvRating)
        etReview = findViewById(R.id.etReview)

        // Update TextView when the user changes the rating
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            tvRating.text = "Your Rating: $rating"
        }

        // Handle click event for submit button
        btnSubmitRating.setOnClickListener {
            val rating = ratingBar.rating
            val ratingAsString = Math.round(rating).toString()
            val consumerId = ConsumerActivity.consumerid

            val task = ThreadTaskPostRating(this)
            task.setPostData(consumerId, ConsumerActivity.businessId, ratingAsString)
            task.start()

            Toast.makeText(this, "Submitted Rating: $rating\n ConsumerId: $consumerId\n BusinessId: ${ConsumerActivity.businessId}", Toast.LENGTH_LONG).show()

            Log.d("Submitted Rating:", "$ratingAsString")
            Log.d("Consumer Id", "$consumerId")
            Log.d("Business Id", "${ConsumerActivity.businessId}")

            getAverage(ConsumerActivity.businessId)

            if (etReview.text.toString().isNotEmpty()) {
                val task = ThreadTaskPostReview(this)
                task.setPostData(consumerId, ConsumerActivity.businessId, etReview.text.toString())
                task.start()

                etReview.text.clear()
            }

        }

        val task2 = ThreadTaskGetCardsForReviews(this)
        task2.setPostData(ConsumerActivity.businessId)
        task2.start()

    }

    private fun createTextView(text: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8.dpToPx(resources)
            }
            setText(text)
            setTextColor(ContextCompat.getColor(context, R.color.white)) // Set the text color to white
        }
    }

    private fun createCardView(review: Map<String, String>): CardView {
        return CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16.dpToPx(resources)
            }
            radius = 8.dpToPx(resources).toFloat()
            cardElevation = 4.dpToPx(resources).toFloat()
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))

            val box = LinearLayout(this@RatingActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16.dpToPx(resources))

                addView(createTextView("ConsumerId: ${review["ConsumerId"]}"))
                addView(createTextView("Rating: ${review["Rating"]}"))
                addView(createTextView("Review: ${review["Review"]}"))
            }

            addView(box)
        }
    }

    private fun Int.dpToPx(resources: Resources): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun getAverage(businessId: String) {
        Log.d("Average:", "2nd time running")
        val task = ThreadTaskAverageRating(businessId) { averageRating ->
            runOnUiThread {
                Log.d("Average:", "$averageRating")
                ConsumerActivity.averageRating = averageRating
            }
        }
        task.start()
    }

    fun parseServerResponse(response: String): List<Map<String, String>> {
        // Split the response into entries by the "<br />" delimiter
        val entries = response.split("<br />").filter { it.isNotBlank() }

        // Initialize a list to hold the structured data
        val reviews = mutableListOf<Map<String, String>>()

        // Regex pattern to match the key-value pairs in the response
        val pattern = "ConsumerId: (.*?), Rating: (.*?), Review: (.*)".toRegex()

        // Loop through each entry
        for (entry in entries) {
            // Match the pattern and group the results
            val matchResult = pattern.find(entry)

            // If there's a match, create a map with the details
            matchResult?.let {
                val consumerId = it.groups[1]?.value?.trim() ?: ""
                val rating = it.groups[2]?.value?.trim() ?: ""
                val review = it.groups[3]?.value?.trim()?.removeSuffix("<br />") ?: ""

                // Add to the list as a map
                reviews.add(mapOf("ConsumerId" to consumerId, "Rating" to rating, "Review" to review))
            }
        }

        return reviews
    }


    override fun updateView(result: String) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show()
        getAverage(ConsumerActivity.businessId)

        val parsedData = parseServerResponse(result)
        val container = findViewById<LinearLayout>(R.id.container)

        // Clear any existing views in the container
        container.removeAllViews()

        // Create and add a CardView for each parsed review entry, skipping entries without a review
        parsedData.forEach { review ->
            // Skip the entry if the review is empty or blank
            if (review["Review"].isNullOrBlank()) {
                return@forEach
            }

            val cardView = createCardView(review)
            container.addView(cardView)
        }

        // Create and add a CardView for each parsed review entry: this shows all entries regardless of whether there is a review or not
        /*
        parsedData.forEach { review ->
            val cardView = createCardView(review)
            container.addView(cardView)
        }
        */

    }

    override fun runOnUi(runnable: Runnable) {
        this@RatingActivity.runOnUiThread(runnable)
    }

}
