package com.example.abox

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abox.cra.TrainingsRecyclerAdapter
import com.example.abox.db.DBManager
import com.example.abox.db.entities.Training
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TrainingsActivity : AppCompatActivity() {

    private companion object {
        const val SETTINGS_ID = 1
        const val DELETE_ALL_ID = 2
    }

    private var db: DBManager? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: TrainingsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_boxing_workout)

        db = DBManager(this)
        db?.open()

        val allTrainings = fetchTrainings()

        adapter = TrainingsRecyclerAdapter(
            trainings = allTrainings,
            onLongClick = {
                onTrainingLongClick(it)
            },
        )

        recyclerView = findViewById(R.id.clWorkoutTitle)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter

        findViewById<FloatingActionButton>(R.id.btnAddTraining).also {
            it.setOnClickListener {
                val intent = Intent(this, CreateTraining().javaClass)
                intent.putExtra("", Training("", "", mutableListOf()))
                startActivityForResult(intent, 1)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db?.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, SETTINGS_ID, 0, "Setting")
        menu?.add(0, DELETE_ALL_ID, 0, "Delete all")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            SETTINGS_ID -> {
                startActivity(Intent(this, BoxerMovementLibrary().javaClass))
            }
            DELETE_ALL_ID -> {
                db?.deleteAllTrainings()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createTrainingsList(cursor: Cursor?): MutableList<Training> {
        val trainings = mutableListOf<Training>()
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                val indexId = cursor.getColumnIndex("_id")
                val indexTitle = cursor.getColumnIndex("workout_title")
                val indexExercises = cursor.getColumnIndex("exercises")

                do {
                    val id = cursor.getString(indexId)
                    val title = cursor.getString(indexTitle)
                    val exercises = cursor.getString(indexExercises).split("|").toMutableList()
                    trainings.add(Training(id, title, exercises))

                } while (cursor.moveToNext())
            } else {
                Toast.makeText(this, "Workout list is empty", Toast.LENGTH_SHORT).show()
            }
        }
        return trainings
    }

    private fun deleteTraining(training: Training) {
        db?.deleteTraining(training)
    }

    private fun fetchTrainings() : List<Training>{
        val cursor = db?.getAllTrainings()
        startManagingCursor(cursor)
        return createTrainingsList(cursor)
    }

    private fun onTrainingLongClick(training: Training) {
        deleteTraining(training)
        val allTrainings = fetchTrainings()
        adapter?.updateTrainings(allTrainings)
    }
}