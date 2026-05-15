package com.example.finalassessmentmulti_viewapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)

        // Initialize SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Load existing name if available
        val existingName = sharedPref.getString("user_name", "")
        etName.setText(existingName)

        btnSave.setOnClickListener {
            val newName = etName.text.toString()

            if (newName.isNotEmpty()) {
                // Save the name to storage
                with(sharedPref.edit()) {
                    putString("user_name", newName)
                    apply()
                }
                Toast.makeText(this, "Preferences Saved!", Toast.LENGTH_SHORT).show()
                finish() // Returns user to the Main Menu
            } else {
                etName.error = "Please enter a name"
            }
        }
    }
}