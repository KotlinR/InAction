package com.action.round.ui.screens.training

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.action.round.Dependencies.Companion.dependencies
import com.action.round.R
import com.action.round.data.models.Training
import com.action.round.ui.recycler.SimpleItemTouchHelperCallback
import com.action.round.ui.screens.timer.TimerActivity
import com.action.round.utills.hideKeyboard
import com.action.round.utills.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

class TrainingActivity : ComponentActivity() {

    companion object {
        private const val KEY_TRAINING = "training"
        private const val BACK_PRESS_TIME_MS = 2000L
        private const val MAX_NUMBER_OF_ROUND = 16

        fun buildIntent(activity: ComponentActivity, training: Training?): Intent {
            return Intent(activity, TrainingActivity::class.java).putExtra(KEY_TRAINING, training)
        }
    }

    private val fab by lazy { findViewById<FloatingActionButton>(R.id.btnAddRound) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvAllRoundsAtTraining) }
    private val tvTrainingTitle by lazy { findViewById<TextView>(R.id.etTrainingTitle) }
    private val tvRounds by lazy { findViewById<TextView>(R.id.tvRounds) }
    private val rootView by lazy { findViewById<ViewGroup>(R.id.rootView) }
    private val btnStartTraining by lazy { findViewById<ImageView>(R.id.btnStartTraining) }

    private val viewModel: TrainingViewModel by viewModels {
        dependencies.trainingViewModelFactory
    }

    private var adapter: TrainingRecycleAdapter? = null
    private var training: Training? = null

    private var backPressed = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        setUpTraining()
        initUI()
        initObserves()
        setUpBackPress()
    }

    private fun setUpTraining() {
        training = intent.getTraining()
        viewModel.setTraining(training)
        tvTrainingTitle.text = training?.title
        tvRounds.text = training?.exercises.orEmpty().size.toString()

        if (tvTrainingTitle.text.isEmpty()) tvTrainingTitle.requestFocus()
    }

    private fun initUI() {
        adapter = TrainingRecycleAdapter(
            onSwipe = { id -> viewModel.deleteExercise(id) },
            onMove = { from, to -> viewModel.moveExercise(from, to) },
            onExerciseChange = { id, text -> viewModel.updateExerciseById(id, text) },
        ).also {
            ItemTouchHelper(SimpleItemTouchHelperCallback(
                adapter = it,
                movePermit = true,
                context = this,
                hideKeyboard = { itemView -> itemView.hideKeyboard() },
            )
            ).attachToRecyclerView(recyclerView)
            recyclerView.adapter = it
        }

        fab.setOnClickListener {
            if (viewModel.exercisesSize < MAX_NUMBER_OF_ROUND) {
                rootView.hideKeyboard()
                viewModel.addNewExercise()
            } else {
                toast { "You've reached max number of rounds - $MAX_NUMBER_OF_ROUND" }
            }
        }

        btnStartTraining.setOnClickListener {
            rootView.hideKeyboard()
            adapter?.getList()?.let { currentList -> viewModel.openTimerScreen(currentList) }
        }

        rootView.setOnClickListener(View::hideKeyboard)
    }

    private fun initObserves() {
        viewModel.exercisesLiveData.observe(this) { exercises ->
            adapter?.submitList(exercises.orEmpty())
            tvRounds?.text = exercises.orEmpty().size.toString()
            btnStartTraining.isEnabled = exercises.isNotEmpty()
        }

        viewModel.openSecondScreenLiveData.observe(this) { exercises ->
            if (exercises.isNotEmpty()) {
                val training =
                    Training(title = tvTrainingTitle.text.toString(), exercises = exercises)
                startActivity(TimerActivity.buildIntent(this, training = training))
            }
        }
    }

    private fun Intent.getTraining(): Training? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(KEY_TRAINING, Training::class.java)
        } else {
            @Suppress("DEPRECATION") getParcelableExtra(KEY_TRAINING)
        }
    }

    private fun saveOrUpdateTraining() {
        val training = training
        if (training == null) {
            viewModel.saveTraining(
                title = tvTrainingTitle.text.toString(),
            )
        } else {
            viewModel.updateTraining(
                id = training.id,
                title = tvTrainingTitle.text.toString(),
            )
        }
    }

    private fun setUpBackPress() {
        onBackPressedDispatcher.addCallback(
            owner = this,
            onBackPressedCallback = object : OnBackPressedCallback(enabled = true) {
                override fun handleOnBackPressed() {
                    if (currentFocus != null) {
                        currentFocus?.clearFocus()
                    } else {
                        if (backPressed + BACK_PRESS_TIME_MS > System.currentTimeMillis()) {
                            isEnabled = false
                            if (recyclerView.isNotEmpty()) {
                                if (tvTrainingTitle.text.isEmpty()) {
                                    tvTrainingTitle.text = LocalDate.now().toString()
                                }
                                saveOrUpdateTraining()
                            }
                            onBackPressedDispatcher.onBackPressed()
                        } else {
                            toast { "Press once again to exit!" }
                        }
                        backPressed = System.currentTimeMillis()
                    }
                }
            },
        )
    }
}
