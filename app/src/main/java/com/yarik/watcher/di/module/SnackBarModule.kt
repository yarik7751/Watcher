package com.yarik.watcher.di.module

import com.yarik.watcher.snackbar.flow.SnackBarManager
import com.yarik.watcher.snackbar.flow.SnackBarManagerFlow
import com.yarik.watcher.snackbar.flow.SnackBarManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SnackBarModule {

    @Provides
    @Singleton
    fun provideShackBarManagerImpl() = SnackBarManagerImpl()

    @Provides
    fun provideShackBarManager(impl: SnackBarManagerImpl) = impl as SnackBarManager

    @Provides
    fun provideShackBarManagerFlow(impl: SnackBarManagerImpl) = impl as SnackBarManagerFlow
}