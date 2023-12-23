package com.example.servertask.ThreadTasks

import android.util.Log
import com.example.servertask.Consumer.BusinessSearch
import com.example.servertask.Consumer.ConsumerActivity
import org.json.JSONArray
import java.io.InputStream
import java.net.URL
import java.util.*

class ThreadTaskSearchBusinesses : Thread {
    lateinit var  activity: BusinessSearch
    var result: String = "NOT SET YET"
    private var searchQuery: String

    constructor(fromActivity: BusinessSearch, searchQuery: String) {
        activity = fromActivity
        this.searchQuery = searchQuery
    }

    override fun run() {
        Log.w("MainActivity", "Inside run")
        try {
            val url = URL(ConsumerActivity.URL_PHP_GET_BUSINESS)
            val iStream: InputStream = url.openStream()
            val scan = Scanner(iStream)
            result = ""

            while (scan.hasNext()) {
                result += scan.nextLine()
            }

            val jsonArray = JSONArray(result)
            val filteredBusinessNames = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                val businessName = jsonArray.getString(i)
                // Add businesses that contain the search query
                if (businessName.contains(searchQuery, ignoreCase = true)) {
                    filteredBusinessNames.add(businessName)
                }
            }

            // Use "\n" to separate businesses by a new line
            val namesString = filteredBusinessNames.joinToString("\n")
            activity.runOnUi(Runnable { activity.updateViewWithJson(namesString) })

        } catch (e: Exception) {
            Log.w("MA", "exception: " + e.message)
        }
    }





    inner class UpdateGui: Runnable {
        override fun run() {
            Log.w("MainActivity", "Inside UpdateGui::Runnable::run")
            activity.updateViewWithJson(result)
        }
    }
}