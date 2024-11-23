package com.example.herbeauty

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class DisplayServiceDetailsActivity : AppCompatActivity() {

    private lateinit var serviceNameTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var appointmentDetailsTextView: TextView

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appintment_display)

        // Initialize UI elements
        serviceNameTextView = findViewById(R.id.service_name_textview)
        priceTextView = findViewById(R.id.price_textview)
        categoryTextView = findViewById(R.id.category_textview)
        appointmentDetailsTextView = findViewById(R.id.appointment_details_textview)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("services")

        // Assuming `category` and `serviceId` are passed via Intent
        val category = intent.getStringExtra("category") ?: ""
        val serviceId = intent.getStringExtra("serviceId") ?: ""

        // Fetch service details
        fetchServiceDetails(category, serviceId)

        // Fetch user's appointment details
        fetchAppointmentDetails(serviceId)
    }

    private fun fetchServiceDetails(category: String, serviceId: String) {
        if (category.isEmpty() || serviceId.isEmpty()) {
            Toast.makeText(this, "Invalid category or service ID", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.child(category).child(serviceId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val serviceName = snapshot.child("serviceName").value.toString()
                        val price = snapshot.child("price").value.toString()
                        val imageBase64 = snapshot.child("imageBase64").value.toString()

                        // Populate data in UI
                        serviceNameTextView.text = serviceName
                        priceTextView.text = price
                        categoryTextView.text = category

                        // Optionally, decode and display the image from Base64 if needed
                        // decodeAndDisplayImage(imageBase64)
                    } else {
                        Toast.makeText(this@DisplayServiceDetailsActivity, "Service not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DisplayServiceDetailsActivity, "Failed to fetch service details", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchAppointmentDetails(serviceId: String) {
        // Assuming "appointments" node contains appointment information
        val appointmentRef = FirebaseDatabase.getInstance().getReference("appointments").child(serviceId)

        appointmentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userName = snapshot.child("userName").value.toString()
                    val appointmentDate = snapshot.child("appointmentDate").value.toString()
                    val appointmentTime = snapshot.child("appointmentTime").value.toString()

                    // Display appointment details
                    val appointmentInfo = """
                        User: $userName
                        Date: $appointmentDate
                        Time: $appointmentTime
                    """.trimIndent()

                    appointmentDetailsTextView.text = appointmentInfo
                } else {
                    appointmentDetailsTextView.text = "No appointments found for this service."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DisplayServiceDetailsActivity, "Failed to fetch appointment details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Optional: Decode Base64 image to display
    // private fun decodeAndDisplayImage(imageBase64: String) {
    //     val decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT)
    //     val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    //     findViewById<ImageView>(R.id.service_image_view).setImageBitmap(bitmap)
    // }
}
