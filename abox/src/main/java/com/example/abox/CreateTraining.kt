package com.example.abox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.abox.db.entities.Training

class CreateTraining : AppCompatActivity() {

    private var etRoundTask: EditText? = null
    private var btnOk: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_round_task)

        etRoundTask = findViewById(R.id.etRoundTask)

        btnOk = findViewById<Button?>(R.id.btnOK).also {
            it.setOnClickListener {
                val intent = Intent()
                intent.putExtra("",Training("","", mutableListOf("")))
                intent.putExtra("task", etRoundTask?.text.toString())
                setResult(
                    RESULT_OK,
                    intent
                )
                finish()
            }
        }
    }
}