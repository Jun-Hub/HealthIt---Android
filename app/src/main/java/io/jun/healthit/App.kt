package io.jun.healthit

import android.app.Application
import io.jun.healthit.di.serviceModule
import io.jun.healthit.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Koin Android logger
            androidLogger(Level.ERROR)
            //inject Android context
            androidContext(this@App)
            // use modules
            modules(listOf(viewModelModule, serviceModule))
        }
    }
}