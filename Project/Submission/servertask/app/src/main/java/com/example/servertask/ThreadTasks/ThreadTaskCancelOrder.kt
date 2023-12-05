package com.example.servertask.ThreadTasks

import android.util.Log
import com.example.servertask.UpdateViewInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ThreadTaskCancelOrder(private val activity: UpdateViewInterface) : Thread() {
    private var result = "NOT SET YET"
    private var postData: HashMap<String, String>? = null

    fun setPostData(id: String, services: String) {
        Log.d("ThreadTaskCancelServices", "id=$id, services=$services")
        postData = hashMapOf(
            "ConsumerId" to id.trim(),
            "Service" to services.trim()
        )
    }

    override fun run() {
        GlobalScope.launch(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            Log.d("ThreadTaskCancelServices", "Starting thread task")

            try {
                val url = URL("http://499aitikira.cs.umd.edu/cgi-bin/cancelOrder.php")
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val postDataString = getPostDataString(postData)

                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(postDataString)
                    writer.flush()
                }

                val responseCode = connection.responseCode
                result = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                } else {
                    connection.errorStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                }

                Log.d("ThreadTaskCancelServices", result)

            } catch (e: IOException) {
                Log.e("ThreadTaskCancelServices", "IOException: ${e.message}")
                result = "IOException: ${e.message}"
            } catch (e: Exception) {
                Log.e("ThreadTaskCancelServices", "Exception: ${e.message}")
                result = "Exception: ${e.message}"
            } finally {
                connection?.disconnect()
            }
        }
    }
}

private fun getPostDataString(params: HashMap<String, String>?): String =
    params?.map { (key, value) ->
        "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
    }?.joinToString("&").orEmpty()
