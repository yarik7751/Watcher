package com.yarik.watcher.navigation.observer

import android.util.Log
import com.yarik.watcher.navigation.JoyScreen
import com.yarik.watcher.navigation.router.ScreensFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

private const val ScreensObserverLogTag = "ScreensObserverLogTag"

interface ScreensObserver {

    fun observe(): Flow<JoyScreen<*>>
}

class ScreensObserverImpl @Inject constructor(
    private val tabsObserver: TabsObserver,
    private val screensFlow: ScreensFlow,
) : ScreensObserver {

    override fun observe(): Flow<JoyScreen<*>> {
        return /*combine(
            tabsObserver.observe(),
            screensFlow.screensFlow
        ) { tabScreen, screens ->
            val lastScreen = screens.lastOrNull() ?: return@combine null
            if (lastScreen is MainTabsJDScreen) {
                TabFragment.getScreenByTabItem(tabScreen)
            } else {
                lastScreen
            }.also {
                Log.i(ScreensObserverLogTag, it::class.simpleName.orEmpty())
            }
        }.filterNotNull().distinctUntilChanged()*/flowOf()
    }
}