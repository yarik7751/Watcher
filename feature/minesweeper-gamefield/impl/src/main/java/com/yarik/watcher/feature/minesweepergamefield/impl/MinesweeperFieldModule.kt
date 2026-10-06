package com.yarik.watcher.feature.minesweepergamefield.impl

import androidx.lifecycle.ViewModel
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.core.ui.ViewModelKey
import com.yarik.watcher.feature.minesweepergamefield.api.MinesweeperFieldJoyScreen
import com.yarik.watcher.feature.minesweepergamefield.impl.game.GameEngine
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
class MinesweeperFieldModule {

    @Provides
    @Singleton
    fun provideGameEngine(): GameEngine = GameEngine()

    @Provides
    fun provideCellMapper(): HexCellUiModelMapper = HexCellUiModelMapper()

    @Provides
    fun provideFieldMapper(): MinesweeperFieldUiModelMapper = MinesweeperFieldUiModelMapper()

    @Provides
    @IntoMap
    @ViewModelKey(MinesweeperFieldViewModel::class)
    fun provideMinesweeperFieldViewModel(
        gameEngine: GameEngine,
        cellUiModelMapper: HexCellUiModelMapper,
        fieldUiModelMapper: MinesweeperFieldUiModelMapper,
    ): ViewModel = MinesweeperFieldViewModel(
        gameEngine = gameEngine,
        cellUiModelMapper = cellUiModelMapper,
        fieldUiModelMapper = fieldUiModelMapper,
    )

    @Provides
    @IntoMap
    @ScreenKey(MinesweeperFieldJoyScreen.ROUTE)
    fun provideMinesweeperFieldContent(): ScreenContentProvider = MinesweeperFieldScreenProvider()
}
