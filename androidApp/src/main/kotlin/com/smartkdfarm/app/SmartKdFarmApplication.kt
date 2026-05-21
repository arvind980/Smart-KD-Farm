package com.smartkdfarm.app

import android.app.Application
import com.smartkdfarm.app.di.SharedContainer

class SmartKdFarmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedContainer().start()
    }
}
