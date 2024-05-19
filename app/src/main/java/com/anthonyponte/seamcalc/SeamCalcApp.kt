package com.anthonyponte.seamcalc

import android.app.Application
import com.google.android.material.color.DynamicColors

class SeamCalcApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}