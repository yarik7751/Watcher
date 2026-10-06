package com.yarik.watcher.feature.start.impl

import androidx.lifecycle.ViewModel
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.core.ui.ViewModelKey
import com.yarik.watcher.feature.start.api.StartJoyScreen
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class StartModule {

    @Provides
    @IntoMap
    @ViewModelKey(StartViewModel::class)
    fun provideStartViewModel(): ViewModel = StartViewModel()

    @Provides
    @IntoMap
    @ScreenKey(StartJoyScreen.ROUTE)
    fun provideStartContent(): ScreenContentProvider = StartScreenProvider()
}
