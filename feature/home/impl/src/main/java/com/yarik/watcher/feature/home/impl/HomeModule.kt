package com.yarik.watcher.feature.home.impl

import androidx.lifecycle.ViewModel
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.core.ui.ViewModelKey
import com.yarik.watcher.feature.home.api.HomeJoyScreen
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class HomeModule {

    @Provides
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun provideHomeViewModel(): ViewModel = HomeViewModel()

    @Provides
    @IntoMap
    @ScreenKey(HomeJoyScreen.ROUTE)
    fun provideHomeContent(): ScreenContentProvider = HomeScreenProvider()
}
