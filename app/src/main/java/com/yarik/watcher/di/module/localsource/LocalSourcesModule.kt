package com.yarik.watcher.di.module.localsource

import com.yarik.watcher.logic.source.local.pref.LocalValuesProvider
import com.yarik.watcher.logic.source.local.pref.LocalValuesProviderImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface LocalSourcesModule {

    @Binds
    @Singleton
    fun bindsLocalValues(bind: LocalValuesProviderImpl): LocalValuesProvider
}