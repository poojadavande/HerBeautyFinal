package com.example.herbeauty

import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewServicesActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_services)

        gridLayout = findViewById(R.id.gridLayout)  // The GridLayout you will use to display data

        fetchServicesData()
    }

    private fun fetchServicesData() {
        // Reference to "services" in Firebase Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("services")

        // Fetching data from Firebase
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gridLayout.removeAllViews()  // Remove previous views

                for (categorySnapshot in snapshot.children) {
                    for (serviceSnapshot in categorySnapshot.children) {
                        val serviceName = serviceSnapshot.child("serviceName").value.toString()
                        val price = serviceSnapshot.child("price").value.toString()
                        val imageUrl = serviceSnapshot.child("imageUrl").value.toString()

                        // Create views dynamically for each service
                        createServiceView(serviceName, price, imageUrl)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewServicesActivity, "Failed to fetch data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun createServiceView(serviceName: String, price: String, imageUrl: String) {
        // Create a TextView for the service name
        val serviceNameTextView = TextView(this).apply {
            text = serviceName
            textSize = 16f
        }

        // Create a TextView for the price
        val priceTextView = TextView(this).apply {
            text = "$price"
            textSize = 14f
        }

        // Create an ImageView for the service image
        val imageView = ImageView(this).apply {
            // Load image from URL using Glide
            Glide.with(this@ViewServicesActivity)
                .load(imageUrl) // Load image URL
                .placeholder(R.drawable.place) // Optional: Placeholder image while loading
                .error(R.drawable.error_image) // Optional: Error image if loading fails
                .into(this)
        }

        // Add the views to GridLayout
        gridLayout.apply {
            addView(imageView)
            addView(serviceNameTextView)
            addView(priceTextView)
        }
    }
}
