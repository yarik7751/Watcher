package com.yarik.watcher.di.module.domain

import com.yarik.watcher.features.minesweeper.gamefield.game.GameEngine
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    @Singleton
    fun provideGameGenerator(): GameEngine {
        return GameEngine()
    }
}