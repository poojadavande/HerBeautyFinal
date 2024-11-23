package com.example.herbeauty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Admin_login : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)

        // Set Login Button Click Listener
        loginButton.setOnClickListener {
            handleLogin()
        }
    }
        private fun handleLogin() {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            when {
                email.isEmpty() -> {
                    emailEditText.error = "Please enter your email"
                    return
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailEditText.error = "Please enter a valid email"
                    return
                }
                password.isEmpty() -> {
                    passwordEditText.error = "Please enter your password"
                    return
                }
                email == "admin@example.com" && password == "admin123" -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Add_Categories::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    passwordEditText.text.clear() // Clear password for security
                }
            }
        }

    }
