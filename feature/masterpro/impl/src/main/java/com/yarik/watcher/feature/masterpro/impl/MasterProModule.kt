package com.yarik.watcher.feature.masterpro.impl

import androidx.lifecycle.ViewModel
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.core.ui.ViewModelKey
import com.yarik.watcher.feature.masterpro.api.ClientCardJoyScreen
import com.yarik.watcher.feature.masterpro.api.ClientsJoyScreen
import com.yarik.watcher.feature.masterpro.api.JobCardJoyScreen
import com.yarik.watcher.feature.masterpro.api.JobsJoyScreen
import com.yarik.watcher.feature.masterpro.api.PriceJoyScreen
import com.yarik.watcher.feature.masterpro.api.SettingsJoyScreen
import com.yarik.watcher.feature.masterpro.api.StatsJoyScreen
import com.yarik.watcher.feature.masterpro.impl.clientcard.ClientCardScreenProvider
import com.yarik.watcher.feature.masterpro.impl.clientcard.ClientCardViewModel
import com.yarik.watcher.feature.masterpro.impl.clients.ClientsScreenProvider
import com.yarik.watcher.feature.masterpro.impl.clients.ClientsViewModel
import com.yarik.watcher.feature.masterpro.impl.jobs.JobsScreenProvider
import com.yarik.watcher.feature.masterpro.impl.jobs.JobsViewModel
import com.yarik.watcher.feature.masterpro.impl.jobcard.JobCardScreenProvider
import com.yarik.watcher.feature.masterpro.impl.jobcard.JobCardViewModel
import com.yarik.watcher.feature.masterpro.impl.price.PriceScreenProvider
import com.yarik.watcher.feature.masterpro.impl.price.PriceViewModel
import com.yarik.watcher.feature.masterpro.impl.settings.SettingsScreenProvider
import com.yarik.watcher.feature.masterpro.impl.settings.SettingsViewModel
import com.yarik.watcher.feature.masterpro.impl.stats.StatsScreenProvider
import com.yarik.watcher.feature.masterpro.impl.stats.StatsViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

/** Модуль фичи «МастерPRO»; регистрируется в AppComponent :app. */
@Module
class MasterProModule {

    @Provides
    @IntoMap
    @ViewModelKey(JobsViewModel::class)
    fun provideJobsViewModel(viewModel: JobsViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ViewModelKey(JobCardViewModel::class)
    fun provideJobCardViewModel(viewModel: JobCardViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ScreenKey(JobsJoyScreen.ROUTE)
    fun provideJobsContent(): ScreenContentProvider = JobsScreenProvider()

    @Provides
    @IntoMap
    @ScreenKey(JobCardJoyScreen.ROUTE_PATTERN)
    fun provideJobCardContent(): ScreenContentProvider = JobCardScreenProvider()

    @Provides
    @IntoMap
    @ViewModelKey(ClientsViewModel::class)
    fun provideClientsViewModel(viewModel: ClientsViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ScreenKey(ClientsJoyScreen.ROUTE)
    fun provideClientsContent(): ScreenContentProvider = ClientsScreenProvider()

    @Provides
    @IntoMap
    @ViewModelKey(ClientCardViewModel::class)
    fun provideClientCardViewModel(viewModel: ClientCardViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ScreenKey(ClientCardJoyScreen.ROUTE_PATTERN)
    fun provideClientCardContent(): ScreenContentProvider = ClientCardScreenProvider()

    @Provides
    @IntoMap
    @ViewModelKey(StatsViewModel::class)
    fun provideStatsViewModel(viewModel: StatsViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ScreenKey(StatsJoyScreen.ROUTE)
    fun provideStatsContent(): ScreenContentProvider = StatsScreenProvider()

    @Provides
    @IntoMap
    @ViewModelKey(PriceViewModel::class)
    fun providePriceViewModel(viewModel: PriceViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ScreenKey(PriceJoyScreen.ROUTE)
    fun providePriceContent(): ScreenContentProvider = PriceScreenProvider()

    @Provides
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun provideSettingsViewModel(viewModel: SettingsViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ScreenKey(SettingsJoyScreen.ROUTE)
    fun provideSettingsContent(): ScreenContentProvider = SettingsScreenProvider()
}
