package com.action.round.ui.screens.main

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.action.round.Dependencies.Companion.dependencies
import com.action.round.R
import com.action.round.ui.recycler.SimpleItemTouchHelperCallback
import com.action.round.ui.screens.timer.TimerActivity
import com.action.round.ui.screens.training.TrainingActivity
import com.action.round.utills.hideKeyboard
import com.action.round.utills.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : ComponentActivity() {

    companion object {
        private const val BACK_PRESS_TIME_MS = 2000L
    }

    private val btnTimer by lazy { findViewById<ImageView>(R.id.btnTimer) }
    private val btnSorted by lazy { findViewById<ImageView>(R.id.btnSorted) }
    private val fab by lazy { findViewById<FloatingActionButton>(R.id.btnAddTraining) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvAllTrainingsList) }
    private val tvAllTrainings by lazy { findViewById<EditText>(R.id.tvAllTrainings) }

    private val viewModel: MainViewModel by viewModels {
        dependencies.mainViewModelFactory
    }

    private var adapter: MainRecyclerAdapter? = null

    private var backPressed = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        initObserves()
        setUpBackPress()
    }

    private fun initUI() {
        adapter = MainRecyclerAdapter(
            onClick = { training -> viewModel.openTrainingScreen(training) },
            onSwipe = { training -> viewModel.deleteTraining(training) }
        ).also {
            ItemTouchHelper(SimpleItemTouchHelperCallback(
                adapter = it,
                movePermit = false,
                context = this,
                hideKeyboard = { itemView -> itemView.hideKeyboard() }
            )).attachToRecyclerView(recyclerView)
        }

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter

        fab.setOnClickListener {
            viewModel.openTrainingScreen(training = null)
        }
        btnTimer.setOnClickListener {
            viewModel.openTimerScreen()
        }

        btnSorted.setOnClickListener {
            if (tvAllTrainings.isFocused) {
                tvAllTrainings.hideKeyboard()
            } else {
                viewModel.sortBy()
            }
        }

        tvAllTrainings.apply {
            setOnFocusChangeListener { view, b ->
                if (this.revealOnFocusHint) {
                    viewModel.preSearch()
                    this.setText("")
                }
                if (!b) {
                    this.setText("All trainings")
                    viewModel.getAllTrainings()
                }
                this.doAfterTextChanged {
                    if (this.text.isNotEmpty()) {
                        viewModel.search(it.toString())
                    } else {
                        viewModel.getAllTrainings()
                    }
                }
            }
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

    override fun onRestart() {
        super.onRestart()
        viewModel.getAllTrainings()
    }
}
