package com.example.servertask.ThreadTasks

import android.util.Log
import com.example.servertask.Consumer.ConsumerActivity
import java.io.InputStream
import java.net.URL
import java.util.*

class ThreadTaskUrl : Thread {
    lateinit var  activity: ConsumerActivity
    var result: String = "NOT SET YET"

    constructor(fromActivity: ConsumerActivity) {
        activity = fromActivity
    }

    override fun run() {
        Log.w("MainActivity", "Inside run")
        try {
            // create a URL
            // val url = URL(MainActivity.URL_PLAIN_TEXT);
            // val url = URL(MainActivity.URL_PHP);
            val url = URL(ConsumerActivity.URL_PHP_GET);

            //create an input stream for the URL
            val iStream: InputStream = url.openStream()
            val scan = Scanner(iStream)
            result = ""

            while (scan.hasNext()) {
                result += scan.nextLine()
                Log.w("MainActivity", "result is " + result)
            }

            //activity.updateView(s)
            var updateGui: UpdateGui = UpdateGui()
            activity.runOnUiThread(updateGui)

            //activity.runOnUiThread(Runnable() { fun run() {
            //Log.w("MainActivity", "Inside Runnable::run, result is $result")
            //activity.updateView(result)}})

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