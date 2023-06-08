package com.action.round.ui.screens.timer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.action.round.Dependencies.Companion.dependencies
import com.action.round.R
import com.action.round.data.models.Exercise
import com.action.round.data.models.Training
import com.action.round.ui.screens.training.TrainingRecycleAdapter
import com.action.round.utills.toast

class TimerActivity : AppCompatActivity() {

    companion object {

        private const val KEY_TRAINING = "training"
        private const val BACK_PRESS_TIME_MS = 2000L

        fun buildIntent(activity: ComponentActivity, training: Training?): Intent {
            return Intent(activity, TimerActivity::class.java).putExtra(KEY_TRAINING, training)
        }
    }

    private val tvRoundsInTimer by lazy { findViewById<TextView>(R.id.tvRoundsInTimer) }
    private val tvTrainingTitleInTimer by lazy { findViewById<TextView>(R.id.tvTrainingTitleInTimer) }
    private val tvActualRound by lazy { findViewById<TextView>(R.id.tvActualRound) }
    private val tvDisplayTimer by lazy { findViewById<TextView>(R.id.tvDisplayTimer) }
    private val tvActualExercise by lazy { findViewById<TextView>(R.id.tvActualExercise) }
    private val btnSettingTimer by lazy { findViewById<ImageView>(R.id.btnSettingTimer) }
    private val btnStartAndPause by lazy { findViewById<ImageView>(R.id.btnStartAndPause) }
    private val btnBack by lazy { findViewById<ImageView>(R.id.btnBack) }
    private val btnNext by lazy { findViewById<ImageView>(R.id.btnNext) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvPreviewRounds) }

    private val viewModel: TimerViewModel by viewModels {
        dependencies.timerViewModelFactory
    }

    private val bottomSheet by lazy {
        TimerParametersBottomSheet(
            timerParameters = viewModel.timerParameters,
            totalRounds = totalRounds,
            onResetTimerParameters = { newTimerParameters -> viewModel.resetTimerParameters(newTimerParameters) },
            onRefreshTotalRounds = { newTotalRounds -> tvRoundsInTimer.text = newTotalRounds.toString() },
        )
    }

    private val gray = Color.DKGRAY
    private val timerActivity = this

    private var adapter: TrainingRecycleAdapter? = null
    private var training: Training? = null
    private var totalRounds: Int? = null
    private var exercises: List<Exercise>? = null

    private var backPressed = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        supportActionBar?.hide()

        setUpTraining()
        initUI()
        initObserves()
        setUpBackPress()
    }

    private fun setUpTraining() {
        training = intent.getTraining()
        exercises = training?.exercises
        totalRounds = exercises?.size
        viewModel.setTraining(totalRounds = totalRounds)
    }

    private fun Intent.getTraining(): Training? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(KEY_TRAINING, Training::class.java)
        } else {
            @Suppress("DEPRECATION") getParcelableExtra(KEY_TRAINING)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        adapter = TrainingRecycleAdapter(
            onSwipe = null,
            onMove = null,
            onExerciseChange = null,
            onLongClick = { round ->
                if (!viewModel.trainingStatus && training != null) viewModel.startFromThisRound(
                    totalRounds = totalRounds,
                    numberRound = round,
                )
            },
        )
        adapter?.submitList(newList = exercises.orEmpty())
        recyclerView.adapter = adapter

        tvTrainingTitleInTimer.text = training?.title ?: "TIMER"
        tvActualRound.text = "Start you training!"
        tvActualExercise.text = exercises?.get(0)?.description.orEmpty()
        tvRoundsInTimer.text = totalRounds?.toString() ?: viewModel.timerParameters.totalRounds.toString()

        btnSettingTimer.setOnClickListener {
            bottomSheet.show(supportFragmentManager, TimerParametersBottomSheet.TAG)
            // TODO (
            //  при открытии установить параметры из SP
            //  при закрытии сохранить только измененые параметры
            //  учитываем статус тренировки true/false
            //  )
        }

        btnStartAndPause.apply {
            setOnClickListener {
                if (!viewModel.trainingStatus) {
                    viewModel.startTraining()
                    btnStartAndPause.setImageResource(android.R.drawable.ic_media_pause)
                    btnNext.makeMuted()
                    btnBack.makeMuted()
                    btnSettingTimer.makeMuted()
                } else {
                    toast { "Hold long to stop" }
                }
            }
            setOnLongClickListener {
                if (viewModel.trainingStatus) {
                    viewModel.pauseTraining()
                    btnStartAndPause.setImageResource(android.R.drawable.ic_media_play)
                    btnNext.makeActive()
                    btnBack.makeActive()
                    btnSettingTimer.makeActive()
                    toast { "Training paused" }
                }
                true
            }
        }

        btnBack.apply {
            setOnClickListener {
                if (!viewModel.trainingStatus) viewModel.back(totalRounds = totalRounds)
            }
            setOnLongClickListener {
                if (!viewModel.trainingStatus) viewModel.resetToStart(totalRounds = totalRounds)
                toast { "Training from the beginning" }
                true
            }
        }

        btnNext.setOnClickListener {
            if (!viewModel.trainingStatus) viewModel.next()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initObserves() {
        viewModel.apply {
            actualTimeLiveData.observe(timerActivity) { actualTime ->
                tvDisplayTimer.text = actualTime
            }
            actualRoundLiveData.observe(timerActivity) { actualRound ->
                tvActualRound.text = actualRound.second
                tvActualExercise.text = exercises?.get(actualRound.first - 1)?.description.orEmpty()
                if (training != null && actualRound.first < totalRounds!!) {
                    recyclerView.scrollToPosition(actualRound.first)
                }
            }
        }
    }

    private fun setUpBackPress() {
        onBackPressedDispatcher.addCallback(
            owner = this,
            onBackPressedCallback = object : OnBackPressedCallback(enabled = true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.trainingStatus) {
                        if (backPressed + BACK_PRESS_TIME_MS > System.currentTimeMillis()) {
                            isEnabled = false
                            viewModel.updateTimerParametersDB()
                            onBackPressedDispatcher.onBackPressed()
                        } else {
                            toast { "Press once again to exit!" }
                        }
                        backPressed = System.currentTimeMillis()
                    }
                }
            }
        )
    }

    private fun ImageView.makeMuted() {
        isEnabled = false
        setColorFilter(gray)
    }

    private fun ImageView.makeActive() {
        isEnabled = true
        clearColorFilter()
    }
}