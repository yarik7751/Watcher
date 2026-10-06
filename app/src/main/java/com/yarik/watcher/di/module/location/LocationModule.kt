package com.yarik.watcher.di.module.location

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}