package com.yarik.watcher.di.module

import com.yarik.watcher.utils.date.DateFormatter
import com.yarik.watcher.utils.date.DateFormatterImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DateTimeModule {

    @Binds
    @Singleton
    fun provideDateFormatter(impl: DateFormatterImpl): DateFormatter
}