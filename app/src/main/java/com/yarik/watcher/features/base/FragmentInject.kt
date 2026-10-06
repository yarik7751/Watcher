package com.yarik.watcher.features.base

import androidx.fragment.app.Fragment
import com.yarik.watcher.application.WatcherApplication
import com.yarik.watcher.features.start.StartFragment
import com.yarik.watcher.features.home.HomeFragment
import com.yarik.watcher.features.calendar.CalendarFragment
import com.yarik.watcher.features.layoutsandbox.LayoutSandboxFragment
import com.yarik.watcher.features.minesweeper.MinesweeperFragment
import com.yarik.watcher.features.minesweeper.gamefield.MinesweeperFieldFragment
import com.yarik.watcher.features.minesweeper.settings.MinesweeperSettingsFragment
import com.yarik.watcher.features.rendernode.RenderNodeFragment
import com.yarik.watcher.features.subcomposelayoutsandbox.SubcomposeLayoutSandboxFragment

fun Fragment.injectDagger(application: WatcherApplication) {
    when (this) {
        is StartFragment -> application.appComponent.inject(this)
        is HomeFragment -> application.appComponent.inject(this)
        is CalendarFragment -> application.appComponent.inject(this)
        is RenderNodeFragment -> application.appComponent.inject(this)
        is LayoutSandboxFragment -> application.appComponent.inject(this)
        is SubcomposeLayoutSandboxFragment -> application.appComponent.inject(this)
        is MinesweeperFragment -> application.appComponent.inject(this)
        is MinesweeperFieldFragment -> application.appComponent.inject(this)
        is MinesweeperSettingsFragment -> application.appComponent.inject(this)
    }
}
