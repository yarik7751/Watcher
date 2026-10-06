package com.yarik.watcher.di.module.navigation

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Cicerone.Companion.create
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.yarik.watcher.navigation.TabsCiceroneHolder
import com.yarik.watcher.navigation.observer.TabsEmitter
import com.yarik.watcher.navigation.observer.TabsObserver
import com.yarik.watcher.navigation.observer.TabsObserverImpl
import com.yarik.watcher.navigation.router.CurrentArgs
import com.yarik.watcher.navigation.router.JoyRouter
import com.yarik.watcher.navigation.router.ScreensFlow
import com.yarik.watcher.navigation.tabsmanager.TabsManager
import com.yarik.watcher.navigation.tabsmanager.TabsManagerImpl
import com.yarik.watcher.navigation.tabsmanager.TabsManagerListener
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NavigationModule {

    private val cicerone: Cicerone<Router> = create()

    @Provides
    @Singleton
    fun provideRouter(): Router {
        return cicerone.router
    }

    @Provides
    @Singleton
    fun provideJDRouter(router: Router): JoyRouter {
        return JoyRouter(router)
    }

    @Provides
    fun provideScreenFlow(jdRouter: JoyRouter) = jdRouter as ScreensFlow

    @Provides
    fun provideCurrentArgs(jdRouter: JoyRouter) = jdRouter as CurrentArgs

    @Provides
    @Singleton
    fun provideNavigatorHolder(): NavigatorHolder {
        return cicerone.getNavigatorHolder()
    }

    @Provides
    @Singleton
    fun provideLocalNavigationHolder(): TabsCiceroneHolder = TabsCiceroneHolder()

    @Provides
    @Singleton
    fun provideTabsObserverImpl() = TabsObserverImpl()

    @Provides
    fun provideTabsEmitter(impl: TabsObserverImpl) = impl as TabsEmitter

    @Provides
    fun provideTabsObserver(impl: TabsObserverImpl) = impl as TabsObserver

    @Provides
    @Singleton
    fun provideTabsManager() = TabsManagerImpl()

    @Provides
    fun provideTabsManagerEmitter(impl: TabsManagerImpl) = impl as TabsManager

    @Provides
    fun provideTabsManagerListener(impl: TabsManagerImpl) = impl as TabsManagerListener
}