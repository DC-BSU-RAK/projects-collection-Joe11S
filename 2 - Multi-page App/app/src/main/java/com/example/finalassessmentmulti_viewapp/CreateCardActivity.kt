package com.example.finalassessmentmulti_viewapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_card)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etRecipient = findViewById<EditText>(R.id.etRecipient)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val spinnerPresets = findViewById<Spinner>(R.id.spinnerPresets)
        val btnPreview = findViewById<Button>(R.id.btnPreview)

        btnPreview.setOnClickListener {
            val recipient = etRecipient.text.toString()
            val message = etMessage.text.toString()
            val preset = spinnerPresets.selectedItem.toString()

            if (recipient.isNotEmpty() && message.isNotEmpty()) {
                // Pass data to the final DisplayActivity
                val intent = Intent(this, DisplayCardActivity::class.java).apply {
                    putExtra("EXTRA_RECIPIENT", recipient)
                    putExtra("EXTRA_MESSAGE", message)
                    putExtra("EXTRA_PRESET", preset)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}