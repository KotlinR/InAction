package com.example.abox

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.example.abox.db.DBManager

class ABoxApplication : Application() {

    companion object {
        fun Context.dbManager(): DBManager {
            return when (this) {
                is ABoxApplication -> db
                else -> applicationContext.dbManager()
            }
        }
    }

    private val db: DBManager by lazy {
        DBManager(this).apply { open() }
    }

    override fun onCreate() {
        super.onCreate()
        db

        registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {
                var activities = 0

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    activities++
                }

                override fun onActivityStarted(activity: Activity) = Unit

                override fun onActivityResumed(activity: Activity) = Unit

                override fun onActivityPaused(activity: Activity) = Unit

                override fun onActivityStopped(activity: Activity) = Unit

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) =
                    Unit

                override fun onActivityDestroyed(activity: Activity) {
                    activities--
                    if (activities == 0) {
                        db.close()
                    }
                }
            },
        )
    }
}
