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
        const val SETTINGS_ID = 1
        const val SAVE_ID = 2
        const val DELETE_ALL_ID = 3
        const val DELETE_TRAINING_ID = 4

        const val CONFIRMATION_DIALOG_TAG = "conf_dialog"
        const val TEXT_DIALOG_TAG = "text_dialog"

        const val SAVE_DIALOG_TYPE = "save_dialog_type"
        const val UPDATE_DIALOG_TYPE = "update_dialog_type"
        const val DELETE_DIALOG_TYPE = "delete_dialog_type"
        const val EDIT_TITLE_DIALOG_TYPE = "edit_title_dialog_type"
        const val EDIT_EXERCISE_DIALOG_TYPE = "edit_exercise_dialog_type"
        const val CREATE_EXERCISE_DIALOG_TYPE = "create_exercise_dialog_type"
    }

    private var db: DBManager? = null

    private var recycleView: RecyclerView? = null
    private var adapter: RoundsRecycleAdapter? = null

    private var originTraining: Training? = null
    private val exercises: MutableList<String> = mutableListOf()
    private var trainingTitle: TextView? = null
    private var rounds: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_activity)

        trainingTitle = findViewById(R.id.tvTrainingTitle)
        rounds = findViewById(R.id.tvRounds)

        val training = intent.getParcelableExtra<Training>("training")

        if (training == null) {
            originTraining = training
            callDialogEnterText(
                text = "New training",
                positiveBtn = "Ok",
                negativeBtn = "Cancel",
                neutralBtn = "Clear",
                title = "Training title",
                type = EDIT_TITLE_DIALOG_TYPE,
                index = null,
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
                    positiveBtn = "OK",
                    negativeBtn = "CANCEL",
                    neutralBtn = "CLEAR",
                    title = "Specify exercise",
                    type = CREATE_EXERCISE_DIALOG_TYPE,
                    index = null,
                )
            } else {
                Toast.makeText(this, "Max 16 rounds", Toast.LENGTH_SHORT).show()
            }
        }

        trainingTitle?.setOnClickListener {
            callDialogEnterText(
                text = trainingTitle?.text.toString(),
                positiveBtn = "OK",
                negativeBtn = "CANCEL",
                neutralBtn = "CLEAR",
                title = "Edit training title",
                type = EDIT_TITLE_DIALOG_TYPE,
                index = null
            )
        }
    }

    override fun onBackPressed() {
        if (originTraining?.title != trainingTitle?.text || originTraining?.exercises != exercises) {
            callConfirmationDialog(
                message = "[ ${trainingTitle?.text} ]",
                yesBtn = "Yes",
                noBtn = "No",
                cancelBtn = "CANCEL",
                title = "Save training?",
                type = SAVE_DIALOG_TYPE,
                index = null,
            )
        } else {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, SETTINGS_ID, 0, "Setting")
        menu?.add(0, SAVE_ID, 0, "Save")
        menu?.add(0, DELETE_ALL_ID, 0, "Delete all rounds")
        menu?.add(0, DELETE_TRAINING_ID, 0, "Delete training")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            SETTINGS_ID -> {
                TODO()
            }
            SAVE_ID -> {
                callConfirmationDialog(
                    message = "[ ${trainingTitle?.text} ]",
                    yesBtn = "YES",
                    noBtn = "NO",
                    cancelBtn = "CANCEL",
                    title = "Save training?",
                    type = SAVE_DIALOG_TYPE,
                    index = null,
                )
            }
            DELETE_ALL_ID -> {
                deleteAllRounds()
            }
            DELETE_TRAINING_ID -> {
                TODO()
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
        callDialogEnterText(
            text = exercises[exerciseIndex],
            positiveBtn = "OK",
            negativeBtn = "CANCEL",
            neutralBtn = "CLEAR",
            title = "Edit exercise",
            type = EDIT_EXERCISE_DIALOG_TYPE,
            index = exerciseIndex
        )
    }

    private fun onRoundLongClick(indexElement: Int) {
        callConfirmationDialog(
            message = "Round [ ${indexElement.plus(1)} ]",
            yesBtn = "YES",
            noBtn = "NO",
            cancelBtn = "Cancel",
            title = "Delete round [ ${indexElement.plus(1)} ]",
            type = DELETE_DIALOG_TYPE,
            index = indexElement
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

    private fun callDialogEnterText(
        text: String,
        positiveBtn: String,
        negativeBtn: String?,
        neutralBtn: String?,
        title: String,
        type: String,
        index: Int?,
    ) {
        EnterTextDialog.newInstance(
            text = text,
            positiveBtn = positiveBtn,
            negativeBtn = negativeBtn,
            neutralBtn = neutralBtn,
            title = title,
            type = type,
            index = index
        ).show(supportFragmentManager, TEXT_DIALOG_TAG)
    }

    private fun callConfirmationDialog(
        message: String,
        yesBtn: String,
        noBtn: String?,
        cancelBtn: String,
        title: String,
        type: String,
        index: Int?,
    ) {
        ConfirmationDialog.newInstance(
            message = message,
            yesBtn = yesBtn,
            noBtn = noBtn,
            cancelBtn = cancelBtn,
            title = title,
            type = type,
            index = index,
        ).show(supportFragmentManager, CONFIRMATION_DIALOG_TAG)
    }

    override fun onDialogSetTextClick(
        dialog: DialogFragment,
        newText: String,
        type: String,
        index: Int?
    ) {
        when (type) {
            EDIT_TITLE_DIALOG_TYPE -> {
                createOrEditTrainingTitle(newText)
                dialog.dismiss()
            }
            CREATE_EXERCISE_DIALOG_TYPE -> {
                addExercise(newText)
                dialog.dismiss()
            }
            EDIT_EXERCISE_DIALOG_TYPE -> {
                editExercise(newText, index)
                dialog.dismiss()
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment, type: String) {
        dialog.tag
        when (type) {
            SAVE_DIALOG_TYPE -> {
                dialog.dismiss()
                finish()
            }
            DELETE_DIALOG_TYPE -> dialog.dismiss()
        }
    }

    override fun onDialogNeutralClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, type: String, index: Int?) {
        when (type) {
            SAVE_DIALOG_TYPE -> {
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
                dialog.dismiss()
                finish()
            }
            UPDATE_DIALOG_TYPE -> {
                if (index != null) {
                    db = DBManager(this).apply {
                        open()
                        updateTraining(
                            Training(
                                id = index.toString(),
                                title = trainingTitle?.text.toString(),
                                exercises = exercises
                            )
                        )
                    }
                    dialog.dismiss()
                    finish()
                }
            }
            DELETE_DIALOG_TYPE -> {
                if (index != null) {
                    deleteRound(index)
                    dialog.dismiss()
                }
            }
        }
    }
}