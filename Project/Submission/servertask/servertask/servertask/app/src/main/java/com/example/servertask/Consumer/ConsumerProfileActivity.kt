package com.example.servertask.Consumer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.R
import com.example.servertask.Start.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ConsumerProfileActivity: AppCompatActivity(){

    private lateinit var CHome: TextView
    private lateinit var profileImageView: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cons_profile)

        CHome = findViewById(R.id.ConsHome)
        profileImageView = findViewById(R.id.profileImageView)

        CHome.text = "Welcome, ${ConsumerActivity.consumerUsername}!"

        logOutButton = findViewById(R.id.logOut)

        logOutButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Log Out")
            builder.setMessage("Are you sure you want to log out?")
            builder.setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            builder.setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            val alertDialog = builder.create()
            alertDialog.show()
        }

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


}