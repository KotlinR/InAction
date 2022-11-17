package com.example.abox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        btnLibrary.setOnClickListener {
            startActivity(Intent(this, BoxerMovementLibrary().javaClass))
        }

        val btnGenerateWorkout = findViewById<Button>(R.id.btnGenerateWorkout)
        btnGenerateWorkout.setOnClickListener {
            startActivity(Intent(this, TrainingsActivity().javaClass))
        }
    }
}