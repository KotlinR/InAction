package com.action.round.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.action.round.R
import com.action.round.data.Training
import com.action.round.data.db.TrainingEntity
import com.action.round.ui.adapter.RoundsRecycleAdapter
import com.action.round.ui.adapter.item.touch.SimpleItemTouchHelperCallback
import com.action.round.ui.dialog.ConfirmationDialog
import com.action.round.ui.dialog.EnterTextDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TrainingActivity : AppCompatActivity(), ConfirmationDialog.InteractionListener,
    EnterTextDialog.InteractionListener {

    companion object {
        private const val DELETE_ROUND_DIALOG = "delete_dialog_type"
        private const val DELETE_TRAINING_DIALOG = "delete_training_dialog_type"
        private const val UPDATE_TRAINING_DIALOG = "update_training_dialog_type"
        private const val EDIT_TITLE_DIALOG = "edit_title_dialog_type"
        private const val EDIT_EXERCISE_DIALOG = "edit_exercise_dialog_type"
        private const val CREATE_EXERCISE_DIALOG = "create_exercise_dialog_type"
        private const val CLOSE_ACTIVITY_DIALOG = "close_activity_dialog_type"

        private const val KEY_TRAINING = "training"

        fun buildIntent(activity: AppCompatActivity, training: Training?): Intent {
            return Intent(activity, TrainingActivity::class.java)
                .putExtra(KEY_TRAINING, training)
        }
    }

    //    private val db by lazy { DBManager(this).also { it.open() } }
    private val fab by lazy { findViewById<FloatingActionButton>(R.id.btnAddRound) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvAllRoundsAtTraining) }
    private val trainingTitle by lazy { findViewById<TextView>(R.id.tvTrainingTitle) }
    private val rounds by lazy { findViewById<TextView>(R.id.tvRounds) }

    private var adapter: RoundsRecycleAdapter? = null
    private var originTraining: TrainingEntity? = null
    private val exercises: MutableList<String> = mutableListOf()

    private var indexElDialog: Int? = null
    private var allTitlesTrainings: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        val training = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_TRAINING, TrainingEntity::class.java)
        } else {
            intent.getParcelableExtra(KEY_TRAINING)
        }

        if (training == null) {
            callDialogEnterText(
                text = "New training",
                positiveBtn = "Ok",
                negativeBtn = "Clear",
                neutralBtn = null,
                title = "Training title",
                tag = EDIT_TITLE_DIALOG,
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
            onSwipe = { index -> onRoundSwipe(index) },
            reversUpdateExercises = { exercises -> reversUpdateExercises(exercises) },
        )

        updateExercises(exercises)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter?.let {
            ItemTouchHelper(
                SimpleItemTouchHelperCallback(it)
            )
                .attachToRecyclerView(recyclerView)
        }

        fab.setOnClickListener {
            if (exercises.size < 16) {
                callDialogEnterText(
                    text = "Exercise",
                    positiveBtn = "Ok",
                    negativeBtn = "Clear",
                    neutralBtn = "Cancel",
                    title = "Specify exercise",
                    tag = CREATE_EXERCISE_DIALOG,
                )
            } else {
                Toast.makeText(this, "Max 16 rounds", Toast.LENGTH_SHORT).show()
            }
        }

        trainingTitle.setOnClickListener {
            callDialogEnterText(
                text = trainingTitle?.text.toString(),
                positiveBtn = "Ok",
                negativeBtn = "Clear",
                neutralBtn = "Cancel",
                title = "Edit training title",
                tag = EDIT_TITLE_DIALOG,
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
                tag = CLOSE_ACTIVITY_DIALOG
            )
        } else {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.apply {
            add(0, TrainingMenu.SAVE.index, 0, TrainingMenu.SAVE.title)
            add(0, TrainingMenu.CLEAR.index, 0, TrainingMenu.CLEAR.title)
            add(0, TrainingMenu.DELETE.index, 0, TrainingMenu.DELETE.title)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            TrainingMenu.SAVE.index -> {
                when {
                    originTraining?.id == null
                            && allTitlesTrainings.contains(trainingTitle?.text) == false -> {
                        saveTraining()
                    }
                    originTraining?.id != null
                            && (allTitlesTrainings.contains(trainingTitle?.text) == false
                            || originTraining?.title == trainingTitle?.text) -> {
                        callConfirmationDialog(
                            message = "The training has been changed. Do you want to save?",
                            positiveBtn = "Save",
                            negativeBtn = null,
                            neutralBtn = "Cancel",
                            title = "Save changes?",
                            tag = UPDATE_TRAINING_DIALOG
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
            TrainingMenu.CLEAR.index -> {
                clearTraining()
            }
            TrainingMenu.DELETE.index -> {
                callConfirmationDialog(
                    message = "The workout will be permanently deleted!",
                    positiveBtn = "Delete",
                    negativeBtn = null,
                    neutralBtn = "Cancel",
                    title = "Delete training?",
                    tag = DELETE_TRAINING_DIALOG
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDialogSetTextClick(
        dialog: DialogFragment,
        newText: String,
    ) {
        when (dialog.tag) {
            EDIT_TITLE_DIALOG -> {
                editTrainingTitle(newText)
                dialog.dismiss()
            }
            CREATE_EXERCISE_DIALOG -> {
                addExercise(newText)
                dialog.dismiss()
            }
            EDIT_EXERCISE_DIALOG -> {
                editExercise(newText, indexElDialog)
                dialog.dismiss()
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when (dialog.tag) {
            UPDATE_TRAINING_DIALOG -> {
                updateTraining()
                dialog.dismiss()
            }
            DELETE_ROUND_DIALOG -> {
                if (indexElDialog != null) {
                    deleteRound(indexElDialog)
                    dialog.dismiss()
                }
            }
            DELETE_TRAINING_DIALOG -> {
                deleteTraining()
                dialog.dismiss()
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        when (dialog.tag) {
            CLOSE_ACTIVITY_DIALOG -> {
                dialog.dismiss()
                finish()
            }
        }
    }

    override fun onDialogNeutralClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    private fun editTrainingTitle(titleText: String) {
        trainingTitle?.text = titleText
    }

    private fun deleteRound(index: Int?) {
        if (index != null) {
            exercises.removeAt(index)
            updateExercises(exercises)
        }
    }

    private fun clearTraining() {
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
            tag = EDIT_EXERCISE_DIALOG,
        )
    }

    private fun onRoundSwipe(indexElement: Int) {
        indexElDialog = indexElement
        callConfirmationDialog(
            message = "Round [ ${indexElement + 1} ]",
            positiveBtn = "Delete",
            negativeBtn = null,
            neutralBtn = "Cancel",
            title = "Delete round?",
            tag = DELETE_ROUND_DIALOG,
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

    private fun reversUpdateExercises(newExercises: List<String>) {
        exercises.clear()
        exercises.addAll(newExercises)
    }

    private fun saveTraining() {
//        db.saveTraining(
//            Training(
//                id = "",
//                title = trainingTitle?.text.toString(),
//                exercises = exercises
//            )
//        )
        Toast.makeText(this, "Training saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateTraining() {
        val updatedTraining = TrainingEntity(
            id = originTraining!!.id,
            title = trainingTitle?.text.toString(),
            exercises = exercises
        )
//        db.updateTraining(updatedTraining)
        originTraining = updatedTraining
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
    }

    private fun deleteTraining() {
        if (originTraining?.id != null) {
            originTraining?.let {
//                db.deleteTraining(it)
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
}