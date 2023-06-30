package com.action.round.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

class Analytics(
    private val firebaseAnalytics: FirebaseAnalytics,
) {

    companion object {
        const val EVENT_PARAM_TIMER_SOURCE_MAIN = "main"
        const val EVENT_PARAM_TIMER_SOURCE_TRAINING = "training"
        private const val EVENT_NAME_TIMER = "timer"
        private const val EVENT_PARAM_NAME_TIMER_SOURCE = "source"
    }

    fun logOpenTimer(source: String) {
        firebaseAnalytics.logEvent(EVENT_NAME_TIMER) {
            param(EVENT_PARAM_NAME_TIMER_SOURCE, source)
        }
    }
}