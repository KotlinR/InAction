package com.action.round.ui.screens.timer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.action.round.Dependencies.Companion.dependencies
import com.action.round.R
import com.action.round.data.models.Training
import com.action.round.ui.screens.training.TrainingRecycleAdapter
import com.action.round.utills.toast

class TimerActivity : ComponentActivity() {

    companion object {

        private const val KEY_TRAINING = "training"
        private const val BACK_PRESS_TIME_MS = 2000L

        fun buildIntent(activity: ComponentActivity, training: Training): Intent {
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

    private var adapter: TrainingRecycleAdapter? = null
    private var training: Training? = null

    private var backPressed = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        setUpTraining()
        initUI()
        initObserves()
        setUpBackPress()
    }

    private fun setUpTraining() {
        training = intent.getTraining()
        viewModel.setTraining(training)
        tvTrainingTitleInTimer.text = training?.title
        tvRoundsInTimer.text = training?.exercises.orEmpty().size.toString()
    }

    private fun Intent.getTraining(): Training? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(KEY_TRAINING, Training::class.java)
        } else {
            @Suppress("DEPRECATION") getParcelableExtra(KEY_TRAINING)
        }
    }

    private fun initUI() {
        adapter = TrainingRecycleAdapter(
            onSwipe = null,
            onMove = null,
            onExerciseChange = null,
            onLongClick = { viewModel.pauseTraining() },
        )
        recyclerView.adapter = adapter

        btnSettingTimer.setOnClickListener {
            // TODO (
            //  при открытии установить параметры из SP
            //  при закрытии сохранить только измененые параметры
            //  )
        }

        btnStartAndPause.setOnClickListener {
            if (!viewModel.trainingStatus) {
                btnStartAndPause.setImageResource(android.R.drawable.ic_media_pause)
                viewModel.startTraining()
            } else {
                btnStartAndPause.setImageResource(android.R.drawable.ic_media_play)
                viewModel.pauseTraining()
            }
        }

        btnBack.setOnClickListener {
            // todo ( плюсуем раунда, отматываем эксерсайс)
        }

        btnNext.setOnClickListener {
            // todo ( минусуем раунда, проматываем эксерсайс)
        }
    }

    private fun initObserves() {
        // TODO (
        //  обновляем:
        //      - время боя
        //      - время отдыха
        //      - заменяем их друг на друга, меняя цвет
        //      - задания на раунд
        //      )

        viewModel.actualTimeLiveData.observe(this) { actualTime ->
            tvDisplayTimer.text = actualTime
        }


        // TODO (переделать и учесть тренировку как null, может и не надо вовсе)
        viewModel.trainingLiveData.observe(this) { training ->
            adapter?.submitList(training?.exercises.orEmpty())
            tvRoundsInTimer.text = training?.exercises?.size.toString()
            tvActualExercise.text = training?.exercises?.get(0)?.description.orEmpty()
            tvActualRound.text = "ROUND 1"
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
}