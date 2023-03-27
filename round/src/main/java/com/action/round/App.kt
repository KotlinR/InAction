package com.action.round

import android.app.Application

class App : Application() {

    val dependencies: Dependencies by lazy { Dependencies(this) }

    override fun onCreate() {
        super.onCreate()
        dependencies
    }
}