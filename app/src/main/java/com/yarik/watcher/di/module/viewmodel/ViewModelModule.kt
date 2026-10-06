package com.yarik.watcher.di.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yarik.watcher.features.base.factory.ViewModelFactory
import com.yarik.watcher.features.start.StartViewModel
import com.yarik.watcher.features.home.HomeViewModel
import com.yarik.watcher.features.layoutsandbox.LayoutSandboxViewModel
import com.yarik.watcher.features.minesweeper.MinesweeperViewModel
import com.yarik.watcher.features.minesweeper.gamefield.MinesweeperFieldViewModel
import com.yarik.watcher.features.minesweeper.settings.MinesweeperSettingsViewModel
import com.yarik.watcher.features.subcomposelayoutsandbox.SubcomposeLayoutSandboxViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(StartViewModel::class)
    fun bindStartViewModel(viewModel: StartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindWidgetsViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LayoutSandboxViewModel::class)
    fun bindLayoutSandboxViewModel(viewModel: LayoutSandboxViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SubcomposeLayoutSandboxViewModel::class)
    fun bindSubcomposeLayoutSandboxViewModel(viewModel: SubcomposeLayoutSandboxViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MinesweeperViewModel::class)
    fun bindMinesweeperViewModel(viewModel: MinesweeperViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MinesweeperFieldViewModel::class)
    fun bindMinesweeperFieldViewModel(viewModel: MinesweeperFieldViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MinesweeperSettingsViewModel::class)
    fun bindMinesweeperSettingsViewModel(viewModel: MinesweeperSettingsViewModel): ViewModel
}