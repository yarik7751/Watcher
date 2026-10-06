package com.yarik.watcher.features.minesweeper.gamefield

import androidx.lifecycle.viewModelScope
import com.yarik.watcher.features.base.BaseViewModel
import com.yarik.watcher.features.minesweeper.gamefield.game.GameEngine
import com.yarik.watcher.features.minesweeper.gamefield.game.model.cell.HexCoordinate
import com.yarik.watcher.features.minesweeper.gamefield.game.model.field.MinesweeperField
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class MinesweeperFieldViewModel @Inject constructor(
    private val gameEngine: GameEngine,
    private val cellUiModelMapper: HexCellUiModelMapper,
    private val fieldUiModelMapper: MinesweeperFieldUiModelMapper,
) : BaseViewModel() {

    private val fieldStateFlow: StateFlow<MinesweeperField> = gameEngine.observe()
        .stateIn(viewModelScope, SharingStarted.Eagerly, MinesweeperField.NotGenerated)

    val fieldUiModelStateFlow: StateFlow<MinesweeperFieldUiModel?> = fieldStateFlow
        .map { fieldUiModelMapper.map(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val cellsStateFlow: StateFlow<List<HexCellUiModel>> = fieldStateFlow
        .map { cellUiModelMapper.map(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        // Временный стартовый размер, пока нет экрана настроек.
        gameEngine.generate(rows = DEFAULT_ROWS, columns = DEFAULT_COLUMNS, minesTotal = DEFAULT_MINES)
    }

    fun onCellClick(coordinates: HexCoordinate) {
        gameEngine.openCell(coordinates)
    }

    fun onCellLongClick(coordinates: HexCoordinate) {
        gameEngine.putFlag(coordinates)
    }

    private companion object {
        const val DEFAULT_ROWS = 15
        const val DEFAULT_COLUMNS = 11
        const val DEFAULT_MINES = 20
    }
}
