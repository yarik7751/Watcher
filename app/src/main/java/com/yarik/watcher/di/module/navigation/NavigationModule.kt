package com.yarik.watcher.di.module.navigation

import com.yarik.watcher.navigation.router.CurrentArgs
import com.yarik.watcher.navigation.router.JoyRouter
import com.yarik.watcher.navigation.router.ScreensFlow
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NavigationModule {

    @Provides
    @Singleton
    fun provideJoyRouter(): JoyRouter = JoyRouter()

    @Provides
    fun provideScreensFlow(router: JoyRouter): ScreensFlow = router

    @Provides
    fun provideCurrentArgs(router: JoyRouter): CurrentArgs = router
}
