package com.yarik.watcher.navigation.tabsmanager

import com.yarik.watcher.navigation.bottomnavigationmenu.BottomNavigationMenuItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

interface TabsManager {

    fun switchTab(item: BottomNavigationMenuItem)
}

interface TabsManagerListener {

    fun observe(): Flow<BottomNavigationMenuItem>
}

class TabsManagerImpl @Inject constructor() : TabsManager, TabsManagerListener {

    private val tabsManagementFlow = MutableSharedFlow<BottomNavigationMenuItem>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override fun switchTab(item: BottomNavigationMenuItem) {
        tabsManagementFlow.tryEmit(item)
    }

    override fun observe(): Flow<BottomNavigationMenuItem> {
        return tabsManagementFlow
    }
}