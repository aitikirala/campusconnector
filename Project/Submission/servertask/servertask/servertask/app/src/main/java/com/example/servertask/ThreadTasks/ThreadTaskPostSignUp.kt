package com.example.servertask.ThreadTasks

import android.util.Log
import com.example.servertask.Consumer.ConsumerActivity
import com.example.servertask.UpdateViewInterface
import java.io.OutputStream
import java.net.URLConnection
import java.net.URL
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.OutputStreamWriter

class ThreadTaskPostSignUp : Thread {
    lateinit var activity: UpdateViewInterface
    var result: String = "NOT SET YET"

    constructor(fromActivity: UpdateViewInterface) {
        activity = fromActivity
    }

    private var postData: HashMap<String, String>? = null

    fun setPostData(data: HashMap<String, String>) {
        this.postData = data
    }


    override fun run() {
        Log.w("MainActivity", "Inside run")
        try {
            var url = if (ConsumerActivity.isBusinessSelected) URL(ConsumerActivity.URL_PHP_POST_BUSINESS) else URL(
                ConsumerActivity.URL_PHP_POST_CONSUMER
            )
            val connection: URLConnection = url.openConnection()
            Log.w("MainActivity", "Inside run, connection open")
            connection.setDoOutput(true) //added by HF

            val os: OutputStream = connection.getOutputStream()
            Log.w("MainActivity", "Inside run after getting output stream")
            val osw = OutputStreamWriter(os)
            Log.w("MainActivity", "Inside run, before writing data")

            // Convert postData HashMap to a POST data string


            val data = postData?.map { "${it.key}=${it.value}" }?.joinToString("&") ?: ""
            Log.w("ThreadTaskPost", "Sending POST data: $data")
            osw.write(data)
            osw.flush()
            Log.w("MainActivity", "Inside run, done posting")
            osw.close() //close output stream
            //call openStream to get an input stream for url

            //call openStream to get an input stream for url
            val iStream = connection.getInputStream()

            //Read data from is
            //Read data from is

            val isr = InputStreamReader(iStream)
            val br = BufferedReader(isr)

            result = ""
            var line: String? = ""
            //while (br.readLine().also { line=it} != null)
            line = br.readLine()
            Log.w("MainActivity", "line is " + line)

            while (line != null) {
                result += line
                line = br.readLine()
            }

            Log.w("MainActivity", "result is " + result)
            isr.close()

            var updateGui: UpdateGui = UpdateGui()
            activity.runOnUi(updateGui)


        } catch (e: Exception) {
            Log.w("MA", "exception: " + e.message)
        }
    }

    inner class UpdateGui : Runnable {
        override fun run() {
            Log.w("MainActivity", "Inside UpdateGui::Runnable::run")
            activity.updateView(result)
        }
    }
}