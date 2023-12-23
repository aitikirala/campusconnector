package com.example.servertask.Business

import ThreadTaskUploadImage
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servertask.Consumer.ConsumerActivity
import com.example.servertask.R
import com.example.servertask.Start.LoginActivity
import com.example.servertask.ThreadTasks.ThreadTaskEnterDescription
import com.example.servertask.ThreadTasks.ThreadTaskEnterServices
import com.example.servertask.ThreadTasks.ThreadTaskViewDescription
import com.example.servertask.UpdateViewInterface

class BusinessProfileActivity : AppCompatActivity(), UpdateViewInterface {

    private lateinit var BHome: TextView
    private lateinit var Description: EditText
    private lateinit var Services: EditText
    private lateinit var profileImageView: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var EditDesc : Button
    private lateinit var EditServices : Button
    private lateinit var responseTextView: TextView
    private lateinit var responseTextView2: TextView
    private lateinit var logOutButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_profile)

        BHome = findViewById(R.id.BusHome)
        profileImageView = findViewById(R.id.profileImageView)

        BHome.text = "Welcome, ${ConsumerActivity.businessUsername}!"

        responseTextView = findViewById(R.id.ResponseTextViewId)
        responseTextView2 = findViewById(R.id.ResponseTextViewId2)

        Description = findViewById(R.id.description)
        Description.visibility = View.GONE

        Services = findViewById(R.id.services)
        Services.visibility = View.GONE

        EditDesc = findViewById(R.id.editDescription)
        EditDesc.setOnClickListener {
            // Toggle visibility
            if (Description.visibility == View.GONE && Description.text.isEmpty()) {
                Description.visibility = View.VISIBLE
            } else if(Description.text.isNotEmpty()) {
                val textEntered = Description.text.toString()
                updateDescription(textEntered)
                Description.text.clear()
                Description.visibility = View.GONE
            } else {
                Description.visibility = View.GONE
            }
        }

        checkDescription()

        logOutButton = findViewById(R.id.logOut)

        logOutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        EditServices = findViewById(R.id.editServices)
        EditServices.setOnClickListener {
            // Toggle visibility
            if (Services.visibility == View.GONE && Services.text.isEmpty()) {
                Services.visibility = View.VISIBLE
            } else if(Services.text.isNotEmpty()){
                val textEntered = Services.text.toString()
                updateServices(textEntered)
                Services.text.clear()
                Services.visibility = View.GONE
            } else {
                Services.visibility = View.GONE
            }
        }

        Description.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // Handle 'Enter' key press
                val textEntered = Description.text.toString()
                updateDescription(textEntered)
                Description.text.clear()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        Services.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // Handle 'Enter' key press
                val textEntered = Services.text.toString()
                updateServices(textEntered);
                Services.text.clear()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        profileImageView.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, pickImage)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImage && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data  // Get the image URI
            profileImageView.setImageURI(imageUri)  // Set the ImageView to the selected image

            // Convert the image to binary data
            val binaryData = convertImageToBinary(imageUri)
            binaryData?.let {
                // Start the thread task to upload the image
                val uploadTask = ThreadTaskUploadImage(it, this)
                uploadTask.start()
            } ?: run {
                Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun convertImageToBinary(imageUri: Uri?): ByteArray? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri!!)
            inputStream?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun updateServices(services : String){
        val task = ThreadTaskEnterServices(this)
        task.setPostData(ConsumerActivity.businessUsername, services)
        task.start()

        Toast.makeText(this, "Services has been updated", Toast.LENGTH_SHORT).show()
    }

    private fun checkDescription(){
        val task = ThreadTaskViewDescription(this)
        task.setPostData(ConsumerActivity.businessUsername)
        task.start()
    }

    private fun updateDescription(description: String) {
        Log.d("BusUserHome", "Updating description: $description for business: ${ConsumerActivity.businessUsername}")
        val task = ThreadTaskEnterDescription(this)
        task.setPostData(ConsumerActivity.businessUsername, description)

        task.start()

        task.join()
        checkDescription()

        Toast.makeText(this, "Your description has been updated.", Toast.LENGTH_LONG).show()
    }

    override fun updateView(result: String) {
        responseTextView.text = result
    }

    override fun runOnUi(runnable: Runnable) {
        this@BusinessProfileActivity.runOnUiThread(runnable)
    }
}
