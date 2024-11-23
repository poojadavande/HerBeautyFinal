package com.example.herbeauty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {

    private lateinit var adminLogin: ImageView
    private lateinit var customerLogin: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Initialize views
        adminLogin = findViewById(R.id.admin1)
        customerLogin = findViewById(R.id.Customer)

        // Set click listeners
        adminLogin.setOnClickListener {
            val intent = Intent(this,Admin_login::class.java)
            startActivity(intent)
        }

        customerLogin.setOnClickListener {
            val intent = Intent(this, LogSigActivity::class.java)
            startActivity(intent)
        }
    }
}
