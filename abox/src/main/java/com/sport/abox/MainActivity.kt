package com.sport.abox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val btnLibrary by lazy { findViewById<Button>(R.id.btnLibrary) }
    private val btnGenerateWorkout by lazy { findViewById<Button>(R.id.btnGenerateWorkout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLibrary.setOnClickListener {
            Toast.makeText(this, "АСТАНАВИТЕСЬ!", Toast.LENGTH_SHORT).show()
        }

       btnGenerateWorkout.setOnClickListener {
            startActivity(Intent(this, AllTrainingsActivity::class.java))
        }
    }
}