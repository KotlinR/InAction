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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.action.round.App
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
            return Intent(activity, TrainingActivity::class.java)
                .putExtra(KEY_TRAINING, training)
        }
    }

    private val fab by lazy { findViewById<FloatingActionButton>(R.id.btnAddRound) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvAllRoundsAtTraining) }
    private val trainingTitle by lazy { findViewById<TextView>(R.id.etTrainingTitle) }
    private val rounds by lazy { findViewById<TextView>(R.id.tvRounds) }

    private val viewModel: TrainingViewModel by viewModels {
        (application as App).dependencies.trainingViewModelFactory
    }

    private var adapter: ExercisesRecycleAdapter? = null
    private var originTraining: Training? = null

    private var indexElDialog: Int? = null
    private var allTitlesTrainings: ArrayList<String> = arrayListOf()
    private var training: Training? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        training = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_TRAINING, Training::class.java)
        } else {
            intent.getParcelableExtra(KEY_TRAINING)
        }

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
                viewModel.saveTraining(
                    Training(
                        id = training?.id,
                        title = trainingTitle.text.toString(),
                        exercises = viewModel.exercisesLiveData.value.orEmpty()
                    )
                )
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

    private fun initUI() {
        adapter = ExercisesRecycleAdapter(
            exercises =,
            onSwipe = { id -> viewModel.deleteExercise(id) },
            onMove = { from, to -> viewModel.moveExercise(from, to) },
        )

        adapter?.let {
            ItemTouchHelper(
                SimpleItemTouchHelperCallback(it)
            )
                .attachToRecyclerView(recyclerView)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            if (exercises.size < 16) {
                viewModel.addNewExercise()
            } else {
                Toast.makeText(this, "Max 16 rounds", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initObserves() {
        viewModel.displayTraining(training)

        viewModel.trainingLiveData.observe(this) { trainingLive ->
            originTraining = trainingLive
            if (trainingLive != null) {
                trainingTitle.text = trainingLive.title
                rounds.text = trainingLive.exercises.size.toString()
                exercises = trainingLive.exercises
            }
        }

        viewModel.exercisesLiveData.observe(this) { exercisesLive ->
            if (exercisesLive == null) return@observe
            exercises = exercisesLive
            adapter?.updateExercises(exercises)
            rounds?.text = exercises.size.toString()
        }
    }
}