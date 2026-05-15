package com.example.finalassessmentmulti_viewapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle Window Insets for Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnCreate = findViewById<Button>(R.id.btnCreateCard)
        val btnHelp = findViewById<Button>(R.id.btnHelp)

        // 1. Load User Preferences
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("user_name", "Guest")
        tvWelcome.text = "Hello, $savedName!"

        // 2. Navigation to Profile
        btnProfile.setOnClickListener {
            // This will open your ProfileActivity once created
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // 3. Navigation to Generator
        btnCreate.setOnClickListener {
            startActivity(Intent(this, CreateCardActivity::class.java))
        }

        // 4. Instructions
        btnHelp.setOnClickListener {
            showInstructions()
        }
    }

    private fun showInstructions() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("App Instructions")
        builder.setMessage("1. Set your name in Profile.\n2. Go to Create Card.\n3. Enter text and pick a theme.\n4. Save your card to the gallery!")
        builder.setPositiveButton("Got it", null)
        builder.show()
    }
}