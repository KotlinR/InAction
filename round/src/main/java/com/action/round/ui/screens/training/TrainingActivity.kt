package com.action.round.ui.screens.training

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.action.round.Dependencies.Companion.dependencies
import com.action.round.R
import com.action.round.data.Training
import com.action.round.ui.adapter.ExercisesRecycleAdapter
import com.action.round.ui.adapter.item.touch.SimpleItemTouchHelperCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TrainingActivity : AppCompatActivity() {

    // TODO (
    //  - навести порядок с List
    //  - реализовать сохраниение
    //  - убрать ненужные диалоги
    //  - вынести инициализацию в отдельные методы
    //  - пофиксить адаптер и обработку действий с элементами
    //  )
    companion object {
        private const val KEY_TRAINING = "training"

        fun buildIntent(activity: AppCompatActivity, training: Training?): Intent {
            return Intent(activity, TrainingActivity::class.java).putExtra(KEY_TRAINING, training)
        }
    }

    private val fab by lazy { findViewById<FloatingActionButton>(R.id.btnAddRound) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvAllRoundsAtTraining) }
    private val trainingTitle by lazy { findViewById<TextView>(R.id.etTrainingTitle) }
    private val rounds by lazy { findViewById<TextView>(R.id.tvRounds) }

    private val viewModel: TrainingViewModel by viewModels {
        dependencies.trainingViewModelFactory
    }

    private var adapter: ExercisesRecycleAdapter? = null
    private var training: Training? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        setUpTraining()
        initUI()
        initObserves()
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
                val training = training
                if (training == null) {
                    viewModel.saveTraining(
                        title = trainingTitle.text.toString(),
                    )
                } else {
                    viewModel.updateTraining(
                        id = training.id,
                        title = trainingTitle.text.toString(),
                    )
                }
            }
            TrainingMenu.CLEAR.index -> {
                viewModel.clearExercises()
            }
            TrainingMenu.DELETE.index -> {
                viewModel.deleteTraining(training)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpTraining() {
        training = intent.getTraining()
        viewModel.setTraining(training)
        // TODO: request|clear focus
        trainingTitle.text = training?.title
        rounds.text = training?.exercises.orEmpty().size.toString()
    }

    private fun initUI() {
        adapter = ExercisesRecycleAdapter(
            onSwipe = { id -> viewModel.deleteExercise(id) },
            onMove = { from, to -> viewModel.moveExercise(from, to) },
            onExerciseChange = { id, text -> viewModel.updateExerciseById(id, text) }
        )

        adapter?.let {
            ItemTouchHelper(SimpleItemTouchHelperCallback(it)).attachToRecyclerView(recyclerView)
        }

        recyclerView.adapter = adapter

        fab.setOnClickListener {
            if (viewModel.exercisesSize < 16) {
                viewModel.addNewExercise()
            } else {
                Toast.makeText(this, "Max 16 rounds", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initObserves() {
        viewModel.exercisesLiveData.observe(this) { exercises ->
            adapter?.submitList(exercises.orEmpty())
            rounds?.text = exercises.orEmpty().size.toString()
        }
    }

    private fun Intent.getTraining(): Training? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(KEY_TRAINING, Training::class.java)
        } else {
            @Suppress("DEPRECATION") getParcelableExtra(KEY_TRAINING)
        }
    }
}
