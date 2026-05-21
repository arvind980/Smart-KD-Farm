package om.smartkdfarm.app

import android.app.Application
import om.smartkdfarm.app.di.SharedContainer

class SmartKdFarmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedContainer().start()
    }
}
