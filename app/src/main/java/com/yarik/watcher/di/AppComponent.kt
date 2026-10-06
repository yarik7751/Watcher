package com.yarik.watcher.di

import android.content.Context
import com.yarik.watcher.MainActivity
import com.yarik.watcher.application.WatcherApplication
import com.yarik.watcher.di.module.CommonModule
import com.yarik.watcher.di.module.DateTimeModule
import com.yarik.watcher.di.module.SnackBarModule
import com.yarik.watcher.di.module.data.DataModule
import com.yarik.watcher.di.module.domain.DomainModule
import com.yarik.watcher.di.module.localsource.LocalSourcesModule
import com.yarik.watcher.di.module.localsource.PreferencesModule
import com.yarik.watcher.di.module.location.LocationModule
import com.yarik.watcher.di.module.navigation.NavigationBindModule
import com.yarik.watcher.di.module.navigation.NavigationModule
import com.yarik.watcher.di.module.network.CommonNetworkModule
import com.yarik.watcher.di.module.network.WeatherNetworkModule
import com.yarik.watcher.di.module.viewmodel.ViewModelModule
import com.yarik.watcher.di.module.worker.WorkerModule
import com.yarik.watcher.features.start.StartFragment
import com.yarik.watcher.features.home.HomeFragment
import com.yarik.watcher.features.calendar.CalendarFragment
import com.yarik.watcher.features.layoutsandbox.LayoutSandboxFragment
import com.yarik.watcher.features.minesweeper.MinesweeperFragment
import com.yarik.watcher.features.minesweeper.gamefield.MinesweeperFieldFragment
import com.yarik.watcher.features.minesweeper.settings.MinesweeperSettingsFragment
import com.yarik.watcher.features.rendernode.RenderNodeFragment
import com.yarik.watcher.features.subcomposelayoutsandbox.SubcomposeLayoutSandboxFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CommonModule::class,
        DateTimeModule::class,
        NavigationModule::class,
        NavigationBindModule::class,
        LocalSourcesModule::class,
        PreferencesModule::class,
        WeatherNetworkModule::class,
        CommonNetworkModule::class,
        DataModule::class,
        DomainModule::class,
        WorkerModule::class,
        ViewModelModule::class,
        SnackBarModule::class,
        LocationModule::class,
    ],
)
interface AppComponent {
    fun inject(app: WatcherApplication)
    fun inject(activity: MainActivity)
    fun inject(fragment: StartFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: CalendarFragment)
    fun inject(fragment: RenderNodeFragment)
    fun inject(fragment: LayoutSandboxFragment)
    fun inject(fragment: SubcomposeLayoutSandboxFragment)
    fun inject(fragment: MinesweeperFragment)
    fun inject(fragment: MinesweeperFieldFragment)
    fun inject(fragment: MinesweeperSettingsFragment)

    @Component.Builder
    interface Builder {
        fun applicationContext(@BindsInstance context: Context): Builder
        fun build(): AppComponent
    }
}