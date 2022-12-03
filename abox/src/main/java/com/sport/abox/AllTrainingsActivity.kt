package com.sport.abox

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abox.R
import com.sport.abox.adapter.TrainingsRecyclerAdapter
import com.sport.abox.db.DBManager
import com.sport.abox.db.entities.Training
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AllTrainingsActivity : AppCompatActivity() {

    private var db: DBManager? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: TrainingsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_trainings_activity)

        db = DBManager(this)
        db?.open()

        val allTrainings = fetchTrainings()

        adapter = TrainingsRecyclerAdapter(
            trainings = allTrainings,
            onLongClick = { onTrainingLongClick(it) },
            onClick = { onTrainingClick(it) },
        )

        recyclerView = findViewById(R.id.rvAllTrainingsList)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter

        findViewById<FloatingActionButton>(R.id.btnAddTraining).setOnClickListener {
            openTrainingScreen(null)
        }
    }

    override fun onRestart() {
        super.onRestart()
        val allTrainings = fetchTrainings()
        adapter?.updateTrainings(allTrainings)
    }

    override fun onDestroy() {
        super.onDestroy()
        db?.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, ItemItMenu.SETTINGS_ID.index, 0, "Setting")
        menu?.add(0, ItemItMenu.DELETE_ALL_ID.index, 0, "Delete all")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            ItemItMenu.SETTINGS_ID.index -> {
                TODO()
            }
            ItemItMenu.DELETE_ALL_ID.index -> {
                if (db?.getAllTrainings()?.count != 0) {
                    db?.deleteAllTrainings()
                }
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
                Toast.makeText(this, "Trainings list is empty", Toast.LENGTH_SHORT).show()
            }
        }
        return trainings
    }

    private fun deleteTraining(training: Training) {
        db?.deleteTraining(training)
    }

    private fun fetchTrainings(): List<Training> {
        val cursor = db?.getAllTrainings()
        startManagingCursor(cursor)
        return createTrainingsList(cursor)
    }

    private fun onTrainingClick(training: Training) {
        openTrainingScreen(training)
    }

    private fun onTrainingLongClick(training: Training) {
        deleteTraining(training)
        val allTrainings = fetchTrainings()
        adapter?.updateTrainings(allTrainings)
    }

    private fun openTrainingScreen(training: Training?) {
        val allTitlesTrainings = arrayListOf<String>()
        fetchTrainings().forEach {
            allTitlesTrainings.add(it.title)
        }
        val intent = Intent(this, TrainingActivity::class.java)
        intent.putExtra("training", training)
        intent.putStringArrayListExtra("allTitleTrainings", allTitlesTrainings)
        startActivity(intent)
    }
}