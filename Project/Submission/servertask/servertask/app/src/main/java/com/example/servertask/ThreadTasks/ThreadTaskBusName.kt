package com.example.servertask.ThreadTasks

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class ThreadTaskBusName(private val BusinessId: String, private val callback: (String?) -> Unit) {
    fun start() {
        // Launch a coroutine on the IO thread for network operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://499aitikira.cs.umd.edu/cgi-bin/findBusName.php?id=$BusinessId")

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"

                    // Read the response
                    inputStream.bufferedReader().use {
                        val response = it.readText()
                        Log.d("Network Response", "Response: $response")

                        // Switch to the Main thread to update UI or invoke callbacks
                        CoroutineScope(Dispatchers.Main).launch {
                            callback(response) // Pass the response directly
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Network Error", "Error: ${e.message}")
                e.printStackTrace()

                // Switch to the Main thread to update UI or invoke callbacks
                CoroutineScope(Dispatchers.Main).launch {
                    callback(null)
                }
            }
        }
    }
}
