package com.example.abox

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abox.adapter.RoundsRecycleAdapter
import com.example.abox.db.DBManager
import com.example.abox.db.entities.Training
import com.example.abox.dialogs.ConfirmationDialog
import com.example.abox.dialogs.EnterTextDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TrainingActivity : AppCompatActivity(), ConfirmationDialog.InteractionListener,
    EnterTextDialog.InteractionListener {

    private companion object {
        const val SAVE_ID = 1
        const val DELETE_ALL_ID = 2
        const val DELETE_TRAINING_ID = 3

        const val DELETE_ROUND_DIALOG_TYPE = "delete_dialog_type"
        const val DELETE_TRAINING_DIALOG_TYPE = "delete_training_dialog_type"
        const val UPDATE_TRAINING_DIALOG_TYPE = "update_training_dialog_type"
        const val EDIT_TITLE_DIALOG_TYPE = "edit_title_dialog_type"
        const val EDIT_EXERCISE_DIALOG_TYPE = "edit_exercise_dialog_type"
        const val CREATE_EXERCISE_DIALOG_TYPE = "create_exercise_dialog_type"
        const val CLOSE_ACTIVITY_DIALOG_TYPE = "close_activity_dialog_type"
    }

    private var db: DBManager? = null

    private var recycleView: RecyclerView? = null
    private var adapter: RoundsRecycleAdapter? = null

    private var originTraining: Training? = null
    private val exercises: MutableList<String> = mutableListOf()
    private var trainingTitle: TextView? = null
    private var rounds: TextView? = null

    private var indexElDialog: Int? = null
    private var allTitlesTrainings: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_activity)

        trainingTitle = findViewById(R.id.tvTrainingTitle)
        rounds = findViewById(R.id.tvRounds)

        val training = intent.getParcelableExtra<Training>("training")
        allTitlesTrainings = intent.getStringArrayListExtra("allTitleTrainings")

        if (training == null) {
            originTraining = training
            callDialogEnterText(
                text = "New training",
                positiveBtn = "Ok",
                negativeBtn = "Clear",
                neutralBtn = null,
                title = "Training title",
                tag = EDIT_TITLE_DIALOG_TYPE,
            )
        } else {
            originTraining = training
            trainingTitle?.text = training.title
            rounds?.text = training.exercises.size.toString()
            exercises.addAll(training.exercises)
        }

        adapter = RoundsRecycleAdapter(
            exercises = mutableListOf(),
            onClick = { index -> onRoundClick(index) },
            onLongClick = { index -> onRoundLongClick(index) },
        )

        updateExercises(exercises)

        recycleView = findViewById(R.id.rvAllRoundsAtTraining)
        recycleView?.layoutManager = LinearLayoutManager(this)
        recycleView?.adapter = adapter

        findViewById<FloatingActionButton>(R.id.btnAddRound).setOnClickListener {
            if (exercises.size < 16) {
                callDialogEnterText(
                    text = "Exercise",
                    positiveBtn = "Ok",
                    negativeBtn = "Clear",
                    neutralBtn = "Cancel",
                    title = "Specify exercise",
                    tag = CREATE_EXERCISE_DIALOG_TYPE,
                )
            } else {
                Toast.makeText(this, "Max 16 rounds", Toast.LENGTH_SHORT).show()
            }
        }

        trainingTitle?.setOnClickListener {
            callDialogEnterText(
                text = trainingTitle?.text.toString(),
                positiveBtn = "Ok",
                negativeBtn = "Clear",
                neutralBtn = "Cancel",
                title = "Edit training title",
                tag = EDIT_TITLE_DIALOG_TYPE,
            )
        }
    }

    override fun onBackPressed() {
        if (originTraining?.title != trainingTitle?.text || originTraining?.exercises != exercises) {
            callConfirmationDialog(
                message = "Changes are not saved. Exit without saving or stay and save?",
                positiveBtn = null,
                negativeBtn = "Exit",
                neutralBtn = "Stay",
                title = "Close activity without saving?",
                tag = CLOSE_ACTIVITY_DIALOG_TYPE
            )
        } else {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val titleSave = if (originTraining?.id == null) {
            "Save training"
        } else "Update training"

        menu?.add(0, SAVE_ID, 0, titleSave)
        menu?.add(0, DELETE_ALL_ID, 0, "Delete all rounds")
        menu?.add(0, DELETE_TRAINING_ID, 0, "Delete training from database")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            SAVE_ID -> {
                when {
                    originTraining?.id == null
                            && allTitlesTrainings?.contains(trainingTitle?.text) == false -> {
                        saveTraining()
                    }
                    originTraining?.id != null
                            && (allTitlesTrainings?.contains(trainingTitle?.text) == false
                            || originTraining?.title == trainingTitle?.text) -> {
                        callConfirmationDialog(
                            message = "The training has been changed. Do you want to save?",
                            positiveBtn = "Save",
                            negativeBtn = null,
                            neutralBtn = "Cancel",
                            title = "Save changes?",
                            tag = UPDATE_TRAINING_DIALOG_TYPE
                        )
                    }
                    else -> {
                        Toast.makeText(
                            this,
                            "A training with the same name already exists!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
            DELETE_ALL_ID -> {
                deleteAllRounds()
            }
            DELETE_TRAINING_ID -> {
                callConfirmationDialog(
                    message = "The workout will be permanently deleted!",
                    positiveBtn = "Delete",
                    negativeBtn = null,
                    neutralBtn = "Cancel",
                    title = "Delete training?",
                    tag = DELETE_TRAINING_DIALOG_TYPE
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createOrEditTrainingTitle(titleText: String) {
        trainingTitle?.text = titleText
    }

    private fun deleteRound(index: Int?) {
        if (index != null) {
            exercises.removeAt(index)
            updateExercises(exercises)
        }
    }

    private fun deleteAllRounds() {
        exercises.clear()
        rounds?.text = ""
        updateExercises(exercises)
    }

    private fun onRoundClick(exerciseIndex: Int) {
        indexElDialog = exerciseIndex
        callDialogEnterText(
            text = exercises[exerciseIndex],
            positiveBtn = "OK",
            negativeBtn = "Clear",
            neutralBtn = "Cancel",
            title = "Edit exercise",
            tag = EDIT_EXERCISE_DIALOG_TYPE,
        )
    }

    private fun onRoundLongClick(indexElement: Int) {
        indexElDialog = indexElement
        callConfirmationDialog(
            message = "Round [ ${indexElement.plus(1)} ]",
            positiveBtn = "Delete",
            negativeBtn = null,
            neutralBtn = "Cancel",
            title = "Delete round?",
            tag = DELETE_ROUND_DIALOG_TYPE,
        )
    }

    private fun editExercise(newExercise: String, index: Int?) {
        if (index != null) {
            exercises[index] = newExercise
            updateExercises(exercises)
        }
    }

    private fun addExercise(newExercise: String) {
        exercises.add(newExercise)
        updateExercises(exercises)
    }

    private fun updateExercises(exercises: List<String>) {
        adapter?.updateRounds(exercises)
        if (exercises.isNotEmpty()) {
            rounds?.text = exercises.size.toString()
        } else {
            rounds?.text = ""
        }
    }

    private fun saveTraining() {
        db = DBManager(this).apply {
            open()
            saveTraining(
                Training(
                    id = "",
                    title = trainingTitle?.text.toString(),
                    exercises = exercises
                )
            )
        }
        Toast.makeText(this, "Training saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateTraining() {
        val updatedTraining = Training(
            id = originTraining?.id.toString(),
            title = trainingTitle?.text.toString(),
            exercises = exercises
        )
        db = DBManager(this).apply {
            open()
            updateTraining(updatedTraining)
        }
        originTraining = updatedTraining
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
    }

    private fun deleteTraining() {
        if (originTraining?.id != null) {
            db = DBManager(this).apply {
                open()
                originTraining?.let {
                    deleteTraining(it)
                }
            }
            Toast.makeText(this, "Training deleted", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "This training is not in the database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun callDialogEnterText(
        text: String,
        positiveBtn: String?,
        negativeBtn: String?,
        neutralBtn: String?,
        title: String,
        tag: String,
    ) {
        EnterTextDialog.newInstance(
            text = text,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn,
            neutralBtn = neutralBtn,
            title = title,
        ).show(supportFragmentManager, tag)
    }

    private fun callConfirmationDialog(
        message: String,
        positiveBtn: String?,
        negativeBtn: String?,
        neutralBtn: String?,
        title: String,
        tag: String,
    ) {
        ConfirmationDialog.newInstance(
            message = message,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn,
            neutralBtn = neutralBtn,
            title = title,
        ).show(supportFragmentManager, tag)
    }

    override fun onDialogSetTextClick(
        dialog: DialogFragment,
        newText: String,
    ) {
        when (dialog.tag) {
            EDIT_TITLE_DIALOG_TYPE -> {
                createOrEditTrainingTitle(newText)
                dialog.dismiss()
            }
            CREATE_EXERCISE_DIALOG_TYPE -> {
                addExercise(newText)
                dialog.dismiss()
            }
            EDIT_EXERCISE_DIALOG_TYPE -> {
                editExercise(newText, indexElDialog)
                dialog.dismiss()
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when (dialog.tag) {
            UPDATE_TRAINING_DIALOG_TYPE -> {
                updateTraining()
                dialog.dismiss()
            }
            DELETE_ROUND_DIALOG_TYPE -> {
                if (indexElDialog != null) {
                    deleteRound(indexElDialog)
                    dialog.dismiss()
                }
            }
            DELETE_TRAINING_DIALOG_TYPE -> {
                deleteTraining()
                dialog.dismiss()
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        when (dialog.tag) {
            CLOSE_ACTIVITY_DIALOG_TYPE -> {
                dialog.dismiss()
                finish()
            }
        }
    }

    override fun onDialogNeutralClick(dialog: DialogFragment) {
        dialog.dismiss()
    }
}