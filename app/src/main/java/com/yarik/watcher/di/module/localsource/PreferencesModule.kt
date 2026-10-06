package com.yarik.watcher.di.module.localsource

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides

@Module
class PreferencesModule {

    @Provides
    fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferencesModule.getSharedPreferences(context)
    }

    companion object {

        fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences("joy_widget_local_values", Context.MODE_PRIVATE)
        }
    }
}