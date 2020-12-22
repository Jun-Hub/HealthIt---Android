package io.jun.healthit

import android.app.Application
import io.jun.healthit.di.billingModule
import io.jun.healthit.di.updateModule
import io.jun.healthit.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
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
            // setup a KoinFragmentFactory instance
            fragmentFactory()
            // use modules
            modules(listOf(viewModelModule, billingModule, updateModule))
        }
    }
}