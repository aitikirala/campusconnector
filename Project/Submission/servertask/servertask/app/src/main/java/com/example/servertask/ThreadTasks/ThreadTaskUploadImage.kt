import com.example.servertask.Consumer.ConsumerActivity
import com.example.servertask.UpdateViewInterface
import okhttp3.*
import java.io.IOException

class ThreadTaskUploadImage(
    private val binaryData: ByteArray,
    private val updateViewInterface: UpdateViewInterface
) : Thread() {

    private val serverUrl = "http://499aitikira.cs.umd.edu/cgi-bin/postImage.php" // Replace with your server's URL

    override fun run() {
        try {
            // Encode the binary data to a Base64 string
            val encodedImage = android.util.Base64.encodeToString(binaryData, android.util.Base64.DEFAULT)

            // Create a multipart form request body
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", ConsumerActivity.businessUsername)  // Add the username part
                .addFormDataPart("logo", encodedImage)  // Add the encoded image part
                .build()

            // Construct the request
            val request = Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build()

            // Execute the request
            OkHttpClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                // Run the update on the UI thread
                updateViewInterface.runOnUi(Runnable {
                    updateViewInterface.updateView(response.body?.string() ?: "Unknown response")
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Run the error update on the UI thread
            updateViewInterface.runOnUi(Runnable {
                updateViewInterface.updateView(e.message ?: "Error occurred")
            })
        }
    }
}
