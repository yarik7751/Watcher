package com.yarik.watcher.application

import android.app.Application
import com.yarik.watcher.di.AppComponent
import com.yarik.watcher.di.DaggerAppComponent
import com.yarik.watcher.features.testgetuserdata.data.UserRepository

class WatcherApplication : Application() {

    lateinit var appComponent: AppComponent

    val userRepository = UserRepository()

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .applicationContext(this)
            .build()
        appComponent.inject(this)
        INSTANCE = this
    }

    companion object {
        internal lateinit var INSTANCE: WatcherApplication
            private set
    }
}