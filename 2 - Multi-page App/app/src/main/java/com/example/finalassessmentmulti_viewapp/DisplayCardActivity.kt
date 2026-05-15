package com.example.finalassessmentmulti_viewapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DisplayCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_display_card)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val container = findViewById<FrameLayout>(R.id.cardContainer)
        val tvTo = findViewById<TextView>(R.id.tvDisplayRecipient)
        val tvMsg = findViewById<TextView>(R.id.tvDisplayMessage)
        val tvFrom = findViewById<TextView>(R.id.tvDisplaySender)
        val btnHome = findViewById<Button>(R.id.btnBackHome)

        // 1. Get Data from Intent
        val recipient = intent.getStringExtra("EXTRA_RECIPIENT")
        val message = intent.getStringExtra("EXTRA_MESSAGE")
        val preset = intent.getStringExtra("EXTRA_PRESET")

        // 2. Get Sender Name from Saved Preferences
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val sender = sharedPref.getString("user_name", "A Friend")

        // 3. Display the Info
        tvTo.text = "To: $recipient"
        tvMsg.text = message
        tvFrom.text = "With love, $sender"

        // 4. Apply Preset Styles
        when (preset) {
            "Birthday Celebration" -> container.setBackgroundResource(R.color.bg_birthday)
            "Romantic Gesture" -> container.setBackgroundResource(R.color.bg_romantic)
            "Professional Thank You" -> container.setBackgroundResource(R.color.bg_thank_you)
            "Just Because" -> container.setBackgroundResource(R.color.bg_just_because)
            else -> container.setBackgroundResource(R.color.container)
        }

        // 5. Navigate Back to Home
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clears the stack
            startActivity(intent)
        }
    }
}