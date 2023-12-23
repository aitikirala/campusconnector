package com.example.servertask.ThreadTasks

import android.util.Log;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;
import org.json.JSONArray
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class ThreadTaskListFav(private val consumerId: String, private val callback: (ArrayList<String>?) -> Unit) {
    fun start() {
        // Launch a coroutine on the IO thread for network operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://499aitikira.cs.umd.edu/cgi-bin/listFav.php?ConsumerId=$consumerId");

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET";

                    // Read the response
                    inputStream.bufferedReader().use {
                        val response = it.readText();
                        Log.d("Network Response", "Response: $response");
                        val favoritesList = parseResponse(response);

                        // Switch to the Main thread to update UI or invoke callbacks
                        CoroutineScope(Dispatchers.Main).launch {
                            callback(favoritesList);
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Network Error", "Error: ${e.message}");
                e.printStackTrace();

                // Switch to the Main thread to update UI or invoke callbacks
                CoroutineScope(Dispatchers.Main).launch {
                    callback(null);
                }
            }
        }
    }

    private fun parseResponse(response: String): ArrayList<String>? {
        return try {
            val favoritesJsonArray = JSONArray(response)
            val favoritesList = ArrayList<String>()

            for (i in 0 until favoritesJsonArray.length()) {
                favoritesList.add(favoritesJsonArray.getString(i))
            }

            for (favorite in favoritesList) {
                Log.d("Favorite Business", "BusinessId: $favorite is a favorite")


            }

            favoritesList
        } catch (e: Exception) {
            Log.e("Parse Function", "Error parsing response: ${e.message}")
            null
        }
    }

}
