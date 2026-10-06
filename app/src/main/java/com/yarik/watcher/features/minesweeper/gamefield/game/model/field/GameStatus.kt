package com.yarik.watcher.features.minesweeper.gamefield.game.model.field

/**
 * Статус заминированного поля.
 * Поле появляется уже в момент первого клика, поэтому
 * состояния «Start» нет — минированное поле всегда активно.
 */
sealed interface GameStatus {
    data object InProgress : GameStatus
    data object Victory : GameStatus
    data object Defeat : GameStatus
}
