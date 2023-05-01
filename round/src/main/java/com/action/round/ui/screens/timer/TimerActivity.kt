package com.action.round.ui.screens.timer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.action.round.R
import com.action.round.data.models.Training

class TimerActivity : ComponentActivity() {

    companion object {

        private const val KEY_TRAINING = "training"

        fun buildIntent(activity: ComponentActivity, training: Training): Intent {
            return Intent(activity, TimerActivity::class.java).putExtra(KEY_TRAINING, training)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
    }

}