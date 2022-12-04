package com.sport.abox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        btnLibrary.setOnClickListener {
            Toast.makeText(this, "АСТАНАВИТЕСЬ!", Toast.LENGTH_SHORT).show()
        }

        val btnGenerateWorkout = findViewById<Button>(R.id.btnGenerateWorkout)
        btnGenerateWorkout.setOnClickListener {
            startActivity(Intent(this, AllTrainingsActivity::class.java))
        }
    }
}