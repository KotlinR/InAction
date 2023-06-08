package com.action.round.ui.screens.main

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.action.round.Dependencies.Companion.dependencies
import com.action.round.R
import com.action.round.ui.screens.timer.TimerActivity
import com.action.round.ui.screens.training.TrainingActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : ComponentActivity() {


    private val btnTimer by lazy { findViewById<ImageView>(R.id.btnTimer) }
    private val fab by lazy { findViewById<FloatingActionButton>(R.id.btnAddTraining) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvAllTrainingsList) }

    private val viewModel: MainViewModel by viewModels {
        dependencies.mainViewModelFactory
    }

    private var adapter: MainRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        initObserves()
    }

    private fun initUI() {
        adapter = MainRecyclerAdapter(
            onLongClick = { training -> viewModel.deleteTraining(training) },
            onClick = { training -> viewModel.openTrainingScreen(training) },
        )

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter

        fab.setOnClickListener {
            viewModel.openTrainingScreen(training = null)
        }
        btnTimer.setOnClickListener {
            viewModel.openTimerScreen()
        }
    }

    private fun initObserves() {
        viewModel.trainingsLiveData.observe(this) { newTrainings ->
            adapter?.submitList(newTrainings)
        }

        viewModel.openSecondScreenTrainingActivityLiveData.observe(this) { training ->
            startActivity(TrainingActivity.buildIntent(activity = this, training = training))
        }

        viewModel.openSecondScreenTimerActivityLiveData.observe(this) {
            startActivity(TimerActivity.buildIntent(activity = this, training = null))
        }
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.getAllTrainings()
    }
}
