package com.yarik.watcher.core.navigation

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory

/**
 * Поставщик контента экрана для графа навигации.
 *
 * Каждая фича-impl предоставляет реализацию и регистрирует её в Dagger-мапе
 * (route → ScreenContentProvider); [com.yarik.watcher.navigation.WatcherNavHost]
 * в :app строит destinations по этой мапе, не зная конкретных экранов.
 */
interface ScreenContentProvider {

    val screen: JoyScreen<*>

    @Composable
    fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter)
}
