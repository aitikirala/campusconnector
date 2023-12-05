
import android.util.Log
import com.example.servertask.Consumer.ConsumerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ThreadTaskConsumerId(private val callback: (String) -> Unit) {

    private var postData: HashMap<String, String>? = null

    fun setPostData() {
        val data = HashMap<String, String>()
        data["username"] = ConsumerActivity.consumerUsername
        postData = data
    }

    fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            var connection: HttpURLConnection? = null
            try {
                // Encode the username and construct the GET URL
                val username = URLEncoder.encode(ConsumerActivity.consumerUsername, "UTF-8")
                val urlString = "http://499aitikira.cs.umd.edu/cgi-bin/findConsId.php?username=$username"
                val url = URL(urlString)

                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("ThreadTaskConsumerId", "Response: $response")
                    withContext(Dispatchers.Main) {
                        callback(response)
                    }
                } else {
                    val errorResponse = connection.errorStream.bufferedReader().use { it.readText() }
                    withContext(Dispatchers.Main) {
                        callback("Error fetching consumer ID: $errorResponse")
                    }
                }
            } catch (e: Exception) {
                Log.e("ThreadTaskConsumerId", "Exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    callback("Exception fetching consumer ID: ${e.message}")
                }
            } finally {
                connection?.disconnect()

            }
        }
    }


}


