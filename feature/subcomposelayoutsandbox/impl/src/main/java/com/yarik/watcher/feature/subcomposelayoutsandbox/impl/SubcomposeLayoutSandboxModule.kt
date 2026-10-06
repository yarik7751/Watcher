package com.yarik.watcher.feature.subcomposelayoutsandbox.impl

import androidx.lifecycle.ViewModel
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.core.ui.ViewModelKey
import com.yarik.watcher.feature.subcomposelayoutsandbox.api.SubcomposeLayoutSandboxJoyScreen
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class SubcomposeLayoutSandboxModule {

    @Provides
    @IntoMap
    @ViewModelKey(SubcomposeLayoutSandboxViewModel::class)
    fun provideSubcomposeLayoutSandboxViewModel(): ViewModel = SubcomposeLayoutSandboxViewModel()

    @Provides
    @IntoMap
    @ScreenKey(SubcomposeLayoutSandboxJoyScreen.ROUTE)
    fun provideSubcomposeLayoutSandboxContent(): ScreenContentProvider =
        SubcomposeLayoutSandboxScreenProvider()
}
