package com.example.servertask.Consumer

import ThreadTaskConsumerId
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.R
import com.example.servertask.ThreadTasks.ThreadTaskBusName
import com.example.servertask.ThreadTasks.ThreadTaskListFav
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListFavActivity : AppCompatActivity() {

    private lateinit var llFavoritesContainer: LinearLayout
    private lateinit var tvConsumerFavorites: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listfav)

        llFavoritesContainer = findViewById(R.id.llFavoritesContainer)
        tvConsumerFavorites = findViewById(R.id.tvConsumerFavorites)

        val consumerName = ConsumerActivity.consumerUsername
        tvConsumerFavorites.text = "$consumerName's Favorites"

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
                // Handle other item clicks if necessary
                else -> false
            }
        }

        fetchAndStoreConsumerId()
    }

    private fun fetchAndStoreConsumerId() {
        ThreadTaskConsumerId { consumerId ->
            runOnUiThread {
                Log.d("ConsumerIdFetcher", "Consumer ID: $consumerId")
                // Assuming consumerId is not null and is a String
                fetchAndDisplayFavorites(consumerId)
            }
        }.execute()
    }

    private fun fetchAndDisplayFavorites(consumerId: String) {
        val task = ThreadTaskListFav(consumerId) { businessIds ->
            if (businessIds != null) {
                runOnUiThread {
                    fetchBusinessNames(businessIds)
                }
            }
        }
        task.start()
    }


    private fun updateViewWithFavorites(listAsNames: ArrayList<String>) {


            Log.d("ListFavActivity", "Favorites List: $listAsNames")



        llFavoritesContainer.removeAllViews()

        for (favorite in listAsNames) {
            val button = Button(this)
            button.text = favorite
            button.setOnClickListener {
                // Handle the button click, perhaps show more details about the favorite
                displayFavoriteDetails(favorite)
            }
            llFavoritesContainer.addView(button)
        }

    }

    private fun fetchBusinessNames(businessIds: List<String>) {
        val names = ArrayList<String>()

        businessIds.forEach { businessId ->
            val task = ThreadTaskBusName(businessId) { name ->
                runOnUiThread {
                    if (name != null) {
                        names.add(name)
                        if (names.size == businessIds.size) {
                            updateViewWithFavorites(names)
                        }
                    }
                }
            }
            task.start()
        }
    }


    private fun displayFavoriteDetails(favorite: String) {
        Toast.makeText(this, "Selected: $favorite", Toast.LENGTH_SHORT).show()
    }
}
