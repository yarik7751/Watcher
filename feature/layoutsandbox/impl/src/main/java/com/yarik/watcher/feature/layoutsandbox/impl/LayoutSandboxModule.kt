package com.yarik.watcher.feature.layoutsandbox.impl

import androidx.lifecycle.ViewModel
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.core.ui.ViewModelKey
import com.yarik.watcher.feature.layoutsandbox.api.LayoutSandboxJoyScreen
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class LayoutSandboxModule {

    @Provides
    @IntoMap
    @ViewModelKey(LayoutSandboxViewModel::class)
    fun provideLayoutSandboxViewModel(): ViewModel = LayoutSandboxViewModel()

    @Provides
    @IntoMap
    @ScreenKey(LayoutSandboxJoyScreen.ROUTE)
    fun provideLayoutSandboxContent(): ScreenContentProvider = LayoutSandboxScreenProvider()
}
