package com.example.herbeauty

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.herbeauty.FirebaseExtraClasses.UserAppointmentHelperClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class AppoinmentActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var mobile: EditText
    private lateinit var email: EditText
    private lateinit var service: EditText
    private lateinit var date: AutoCompleteTextView
    private lateinit var sendWhatsappBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appoinment)
        title = "Appoinment"

        name = findViewById(R.id.name_id)
        mobile = findViewById(R.id.mobile_id)
        email = findViewById(R.id.email_id)
        service = findViewById(R.id.service_id)
        date = findViewById(R.id.date_id)
        sendWhatsappBtn = findViewById(R.id.send_whatsapp_btn)

        date.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePicker()
            }
        }

        sendWhatsappBtn.setOnClickListener {
            val namestr = name.text.toString()
            val mobilestr = mobile.text.toString()
            val emailstr = email.text.toString()
            val servicestr = service.text.toString()
            val datestr = date.text.toString()

            val appointment = UserAppointmentHelperClass(namestr, mobilestr, emailstr, servicestr, datestr)

            val appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments")
            appointmentsRef.child(datestr).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(this@AppoinmentActivity, "This date is already booked. Please select another date.", Toast.LENGTH_SHORT).show()
                    } else {
                        appointmentsRef.child(datestr).setValue(appointment)
                        Toast.makeText(this@AppoinmentActivity, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()

                        val message = "Appointment Alert \n\nName: $namestr \nMobile No: $mobilestr \nEmail: $emailstr \nService Name: $servicestr \nDate: $datestr"
                        sendNotification(message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AppoinmentActivity, "Failed to check appointment availability.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun sendNotification(message: String) {
        val phone = "+91 9284505942" // Owner's phone number
        val whatsappUri = "http://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"

        // Check if WhatsApp is installed
        if (isAppInstalled("com.whatsapp")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(whatsappUri)
            startActivity(intent)
        } else {
            // Fall back to Messenger if WhatsApp is not installed
            Toast.makeText(this, "WhatsApp not installed. Sending via Messenger...", Toast.LENGTH_SHORT).show()
            val messengerUri = "sms:$phone?body=${Uri.encode(message)}"
            val messengerIntent = Intent(Intent.ACTION_VIEW)
            messengerIntent.data = Uri.parse(messengerUri)
            startActivity(messengerIntent)
        }

        // Simulate notification to owner's phone
        sendSmsToOwner(phone, message)
    }

    private fun sendSmsToOwner(phone: String, message: String) {
        val smsUri = Uri.parse("smsto:$phone")
        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
        smsIntent.putExtra("sms_body", message)
        startActivity(smsIntent)
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(this@AppoinmentActivity,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, monthOfYear, dayOfMonth)

                if (selectedCalendar.before(calendar)) {
                    Toast.makeText(this@AppoinmentActivity, "Please select a current or future date", Toast.LENGTH_SHORT).show()
                } else {
                    date.setText("$dayOfMonth-${monthOfYear + 1}-$year")
                }
            }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
}
