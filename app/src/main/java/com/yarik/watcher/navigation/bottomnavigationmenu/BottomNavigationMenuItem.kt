package com.yarik.watcher.navigation.bottomnavigationmenu

enum class BottomNavigationMenuItem {
    Tab1,
    Tab2;

    companion object {
        fun findTabByName(name: String?): BottomNavigationMenuItem? {
            return BottomNavigationMenuItem.entries.find { it.name == name }
        }
    }
}