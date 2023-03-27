package com.action.round.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.action.round.App
import com.action.round.R
import com.action.round.data.Training
import com.action.round.ui.adapter.TrainingsRecyclerAdapter
import com.action.round.ui.vm.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val fab by lazy { findViewById<FloatingActionButton>(R.id.btnAddTraining) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvAllTrainingsList) }

    private val viewModel: MainViewModel by viewModels {
        (application as App).dependencies.mainViewModelFactory
    }

    private var adapter: TrainingsRecyclerAdapter? = null
    private var trainings: List<Training> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        initObserves()
    }

    private fun initUI() {
        adapter = TrainingsRecyclerAdapter(
            trainings = trainings,
            onLongClick = { viewModel.deleteTraining(it) },
            onClick = { viewModel.openTrainingScreen(it) },
        )

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter

        fab.setOnClickListener {
            viewModel.openTrainingScreen(training = null)
        }
    }

    private fun initObserves() {
        viewModel.trainingsLiveData.observe(this) { trainingsLive ->
            trainings = trainingsLive
            adapter?.updateTrainings(trainings)
        }

        viewModel.openSecondScreenLiveData.observe(this) { training ->
            startActivity(TrainingActivity.buildIntent(this, training))
        }
    }
}