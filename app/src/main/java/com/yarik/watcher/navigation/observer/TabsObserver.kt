package com.yarik.watcher.navigation.observer

import com.yarik.watcher.navigation.bottomnavigationmenu.BottomNavigationMenuItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

interface TabsObserver {

    fun observe(): Flow<BottomNavigationMenuItem>
}

interface TabsEmitter {

    fun emitTab(tab: BottomNavigationMenuItem)
}

class TabsObserverImpl @Inject constructor(): TabsObserver, TabsEmitter {
    private val tabsFlow = MutableSharedFlow<BottomNavigationMenuItem>(
        replay = 1,
        extraBufferCapacity = 2,
    )

    override fun observe(): Flow<BottomNavigationMenuItem> {
        return tabsFlow
    }

    override fun emitTab(tab: BottomNavigationMenuItem) {
        tabsFlow.tryEmit(tab)
    }
}