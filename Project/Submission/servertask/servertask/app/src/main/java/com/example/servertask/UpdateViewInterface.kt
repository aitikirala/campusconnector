package com.example.servertask

interface UpdateViewInterface {
    fun updateView(s: String)
    fun runOnUi(runnable: Runnable)

}