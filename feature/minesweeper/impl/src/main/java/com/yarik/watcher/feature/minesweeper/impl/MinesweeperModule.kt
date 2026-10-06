package com.yarik.watcher.feature.minesweeper.impl

import androidx.lifecycle.ViewModel
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.core.ui.ViewModelKey
import com.yarik.watcher.feature.minesweeper.api.MinesweeperJoyScreen
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class MinesweeperModule {

    @Provides
    @IntoMap
    @ViewModelKey(MinesweeperViewModel::class)
    fun provideMinesweeperViewModel(): ViewModel = MinesweeperViewModel()

    @Provides
    @IntoMap
    @ScreenKey(MinesweeperJoyScreen.ROUTE)
    fun provideMinesweeperContent(): ScreenContentProvider = MinesweeperScreenProvider()
}
