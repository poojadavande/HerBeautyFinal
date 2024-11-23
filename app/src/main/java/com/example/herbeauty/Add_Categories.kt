package com.example.herbeauty

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

class Add_Categories : AppCompatActivity() {

    private lateinit var serviceNameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var submitButton: Button
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private var imageBase64: String? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_categories)

        // Initialize UI elements
        serviceNameEditText = findViewById(R.id.service_name)
        priceEditText = findViewById(R.id.prize)
        categoryEditText = findViewById(R.id.category)
        imageView = findViewById(R.id.img)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        submitButton = findViewById(R.id.submitButton)

        // Upload Image Button click listener
        uploadImageButton.setOnClickListener {
            openFileManager()
        }

        // Submit Button click listener
        submitButton.setOnClickListener {
            val serviceName = serviceNameEditText.text.toString()
            val price = priceEditText.text.toString()
            val category = categoryEditText.text.toString().toLowerCase(Locale.ROOT)

            if (serviceName.isEmpty() || price.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else {
                if (imageBase64 != null) {
                    saveServiceDataToDatabase(serviceName, price, category, imageBase64!!)
                } else {
                    Toast.makeText(this, "Please upload an image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Open file manager to select an image
    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle file selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            imageUri = data?.data
            imageUri?.let {
                val bitmap = uriToBitmap(it)
                imageView.setImageBitmap(bitmap) // Display the image in the ImageView
                imageBase64 = bitmapToBase64(bitmap) // Convert the image to Base64
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Convert Uri to Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    // Convert Bitmap to Base64 String
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Save service data (including image Base64) to Realtime Database
    private fun saveServiceDataToDatabase(serviceName: String, price: String, category: String, imageBase64: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("services")
        val serviceId = UUID.randomUUID().toString()

        val serviceData = mapOf(
            "serviceName" to serviceName,
            "price" to price,
            "imageBase64" to imageBase64
        )

        databaseReference.child(category).child(serviceId).setValue(serviceData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Service added successfully!", Toast.LENGTH_LONG).show()
                    clearFields()
                } else {
                    Toast.makeText(this, "Failed to add service.", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Clear input fields after successful upload
    private fun clearFields() {
        serviceNameEditText.text.clear()
        priceEditText.text.clear()
        categoryEditText.text.clear()
        imageView.setImageResource(android.R.drawable.ic_menu_camera) // Reset ImageView
        imageBase64 = null
    }
}
