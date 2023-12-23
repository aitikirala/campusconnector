import android.util.Log
import com.example.servertask.Consumer.ConsumerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ThreadTaskAverageRating(private val businessId: String, private val callback: (Double?) -> Unit) {
    fun start() {
        // Launch a coroutine on the IO thread for network operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://499aitikira.cs.umd.edu/cgi-bin/ratingAvg.php?BusinessId=$businessId")

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"

                    // Read the response
                    inputStream.bufferedReader().use {
                        val response = it.readText()
                        Log.d("Network Response", "Response: $response")
                        val averageRating = parseResponse(response)

                        // Switch to the Main thread to update UI or invoke callbacks
                        CoroutineScope(Dispatchers.Main).launch {
                            ConsumerActivity.averageRating = averageRating
                            Log.d ("Hey", "${ConsumerActivity.averageRating}")
                            callback(averageRating)

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

    private fun parseResponse(response: String): Double? {
        return try {
            val jsonObject = JSONObject(response)
            jsonObject.optDouble("average_rating", -1.0).takeIf { it != -1.0 }
        } catch (e: Exception) {
            Log.e("Parse Function", "Error parsing response: ${e.message}")
            null
        }
    }
}