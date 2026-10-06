package com.yarik.watcher.feature.minesweepersettings.impl

import androidx.lifecycle.ViewModel
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.core.ui.ViewModelKey
import com.yarik.watcher.feature.minesweepersettings.api.MinesweeperSettingsJoyScreen
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class MinesweeperSettingsModule {

    @Provides
    @IntoMap
    @ViewModelKey(MinesweeperSettingsViewModel::class)
    fun provideMinesweeperSettingsViewModel(): ViewModel = MinesweeperSettingsViewModel()

    @Provides
    @IntoMap
    @ScreenKey(MinesweeperSettingsJoyScreen.ROUTE)
    fun provideMinesweeperSettingsContent(): ScreenContentProvider =
        MinesweeperSettingsScreenProvider()
}
