package com.yarik.watcher.feature.calendar.impl

import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.feature.calendar.api.CalendarJoyScreen
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class CalendarModule {

    @Provides
    @IntoMap
    @ScreenKey(CalendarJoyScreen.ROUTE)
    fun provideCalendarContent(): ScreenContentProvider = CalendarScreenProvider()
}
