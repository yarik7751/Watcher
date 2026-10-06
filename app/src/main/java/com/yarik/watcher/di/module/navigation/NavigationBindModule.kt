package com.yarik.watcher.di.module.navigation

import com.yarik.watcher.navigation.observer.ScreensObserver
import com.yarik.watcher.navigation.observer.ScreensObserverImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface NavigationBindModule {

    @Binds
    @Singleton
    fun bindScreensObserver(impl: ScreensObserverImpl): ScreensObserver
}