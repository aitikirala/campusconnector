package com.example.servertask.ThreadTasks

import android.util.Log
import com.example.servertask.Consumer.ConsumerActivity
import com.example.servertask.UpdateViewInterface
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ThreadTaskGetOrders (private val activity: UpdateViewInterface) : Thread() {
    private var result = "NOT SET YET"
    private var postData: HashMap<String, String>? = null


    fun setPostData(id: String) {
        Log.d("ThreadTaskGetOrders", "Setting post data: id=$id")
        postData = hashMapOf(
            "BusinessId" to id,
        )
    }

    override fun run() {
        var connection: HttpURLConnection? = null
        Log.d("ThreadTaskGetOrders", "Starting thread task")

        try {
            val url = URL("http://499aitikira.cs.umd.edu/cgi-bin/getOrders.php")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            Log.d("ThreadTaskGetOrders", "Sending POST data: ${getPostDataString(postData)}")

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(getPostDataString(postData))
                writer.flush()
            }

            val responseCode = connection.responseCode
            result = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                connection.errorStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            }

            Log.d("ThreadTaskGetOrders", "Response from server: $result")

            Log.d("ThreadTaskGetOrders", "Response from server: ${ConsumerActivity.BusinessReceivedOrders}")

        } catch (e: IOException) {
            Log.e("ThreadTaskGetOrders", "IOException: ${e.message}")
            result = "IOException: ${e.message}"
        } catch (e: Exception) {
            Log.e("ThreadTaskGetOrders", "Exception: ${e.message}")
            result = "Exception: ${e.message}"
        } finally {
            connection?.disconnect()
            activity.runOnUi { activity.updateView(result) }
        }
    }

    private fun getPostDataString(params: HashMap<String, String>?): String =
        params?.map { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }?.joinToString("&").orEmpty()

}