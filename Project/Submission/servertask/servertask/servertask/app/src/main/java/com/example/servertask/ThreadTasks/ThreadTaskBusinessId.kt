// ThreadTaskBusinessId.kt
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ThreadTaskBusinessId(private val businessName: String, private val callback: (String) -> Unit) {
    fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            var connection: HttpURLConnection? = null
            try {
                val postDataParams = HashMap<String, String>()
                postDataParams["name"] = businessName

                val url = URL("https://499aitikira.cs.umd.edu/cgi-bin/findBusId.php") // Replace with your server URL

                connection = url.openConnection() as HttpURLConnection
                connection.readTimeout = 15000
                connection.connectTimeout = 15000
                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true

                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes(getPostDataString(postDataParams))
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("ThreadTaskBusinessId", "Response: $response")
                    withContext(Dispatchers.Main) {
                        callback(response)
                    }
                } else {
                    val errorResponse = connection.errorStream.bufferedReader().use { it.readText() }
                    withContext(Dispatchers.Main) {
                        callback("Error fetching business ID: $errorResponse")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback("Exception fetching business ID: ${e.message}")
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    // This function remains unchanged
    private fun getPostDataString(params: HashMap<String, String>): String {
        val result = StringBuilder()
        var first = true

        for ((key, value) in params) {
            if (first) first = false else result.append("&")

            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }
}

