package com.yarik.watcher.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.start.api.StartJoyScreen

/**
 * Граф навигации приложения, собираемый из Dagger-мапы провайдеров контента
 * (route → ScreenContentProvider), которую наполняют Dagger-модули фич.
 *
 * :app не знает о конкретных экранах — добавление фичи не требует правок здесь.
 * Start — start destination; навигация идёт только через [JoyRouter],
 * подключаемый к NavController здесь же.
 */
@Composable
fun WatcherNavHost(
    router: JoyRouter,
    viewModelFactory: ViewModelFactory,
    contentProviders: Map<String, ScreenContentProvider>,
) {
    val navController = rememberNavController()

    DisposableEffect(navController) {
        router.attach(navController, contentProviders.mapValues { it.value.screen })
        onDispose { router.detach() }
    }

    NavHost(
        navController = navController,
        startDestination = StartJoyScreen.route,
    ) {
        contentProviders.forEach { (route, provider) ->
            composable(route) {
                provider.Content(
                    viewModelFactory = viewModelFactory,
                    router = router,
                )
            }
        }
    }
}
