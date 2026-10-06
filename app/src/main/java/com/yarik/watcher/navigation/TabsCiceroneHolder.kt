package com.yarik.watcher.navigation

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Cicerone.Companion.create
import com.yarik.watcher.navigation.bottomnavigationmenu.BottomNavigationMenuItem

class TabsCiceroneHolder {

    var currentTab: BottomNavigationMenuItem = START_TAB
    private val tabsContainer = mutableMapOf<BottomNavigationMenuItem, Cicerone<Router>>()

    fun getCicerone(tabItem: BottomNavigationMenuItem) = tabsContainer.getOrPut(tabItem) {
        create()
    }

    fun getRouter() = getCicerone(currentTab).router

    companion object {
        val START_TAB = BottomNavigationMenuItem.Tab1
    }
}