package com.yarik.watcher.di

import android.content.Context
import com.yarik.watcher.MainActivity
import com.yarik.watcher.application.WatcherApplication
import com.yarik.watcher.core.database.di.DatabaseModule
import com.yarik.watcher.di.module.CommonModule
import com.yarik.watcher.di.module.DateTimeModule
import com.yarik.watcher.di.module.SnackBarModule
import com.yarik.watcher.di.module.data.DataModule
import com.yarik.watcher.di.module.localsource.LocalSourcesModule
import com.yarik.watcher.di.module.localsource.PreferencesModule
import com.yarik.watcher.di.module.location.LocationModule
import com.yarik.watcher.di.module.navigation.NavigationModule
import com.yarik.watcher.di.module.network.CommonNetworkModule
import com.yarik.watcher.di.module.network.WeatherNetworkModule
import com.yarik.watcher.di.module.worker.WorkerModule
import com.yarik.watcher.feature.calendar.impl.CalendarModule
import com.yarik.watcher.feature.home.impl.HomeModule
import com.yarik.watcher.feature.layoutsandbox.impl.LayoutSandboxModule
import com.yarik.watcher.feature.masterpro.impl.MasterProModule
import com.yarik.watcher.feature.minesweeper.impl.MinesweeperModule
import com.yarik.watcher.feature.minesweepergamefield.impl.MinesweeperFieldModule
import com.yarik.watcher.feature.minesweepersettings.impl.MinesweeperSettingsModule
import com.yarik.watcher.feature.rendernode.impl.RenderNodeModule
import com.yarik.watcher.feature.start.impl.StartModule
import com.yarik.watcher.feature.subcomposelayoutsandbox.impl.SubcomposeLayoutSandboxModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CommonModule::class,
        DateTimeModule::class,
        NavigationModule::class,
        StartModule::class,
        HomeModule::class,
        CalendarModule::class,
        RenderNodeModule::class,
        LayoutSandboxModule::class,
        SubcomposeLayoutSandboxModule::class,
        MinesweeperModule::class,
        MinesweeperFieldModule::class,
        MinesweeperSettingsModule::class,
        MasterProModule::class,
        DatabaseModule::class,
        LocalSourcesModule::class,
        PreferencesModule::class,
        WeatherNetworkModule::class,
        CommonNetworkModule::class,
        DataModule::class,
        WorkerModule::class,
        SnackBarModule::class,
        LocationModule::class,
    ],
)
interface AppComponent {
    fun inject(app: WatcherApplication)
    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        fun applicationContext(@BindsInstance context: Context): Builder
        fun build(): AppComponent
    }
}
