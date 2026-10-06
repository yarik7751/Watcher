package com.yarik.watcher.features.base

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.yarik.watcher.features.base.factory.ViewModelFactory
import com.yarik.watcher.application.WatcherApplication
import com.yarik.watcher.designsystem.DesignSystem
import com.yarik.watcher.navigation.TabsCiceroneHolder
import com.yarik.watcher.navigation.router.JoyRouter
import com.yarik.watcher.navigation.tabsmanager.TabsManager
import javax.inject.Inject

abstract class BaseComposeFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var router: JoyRouter

    @Inject
    lateinit var tabsManager: TabsManager

    @Inject
    lateinit var tabsCiceroneHolder: TabsCiceroneHolder

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectDagger(WatcherApplication.INSTANCE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )
            setContent {
                MaterialTheme {
                    Surface(
                        color = DesignSystem.Colors.testBg
                    ) {
                        val view = LocalView.current
                        SideEffect {
                            val window = (view.context as Activity).window
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
                                window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                                    val statusBarInsets =
                                        insets.getInsets(WindowInsets.Type.statusBars())

                                    val navBarInsets =
                                        insets.getInsets(WindowInsets.Type.navigationBars())
                                    view.setBackgroundColor(DesignSystem.Colors.testBlockBg.toArgb())

                                    view.setPadding(0, statusBarInsets.top, 0, navBarInsets.bottom)

                                    insets
                                }
                            } else {
                                window.statusBarColor = DesignSystem.Colors.testBlockBg.toArgb()
                                window.navigationBarColor = DesignSystem.Colors.testBlockBg.toArgb()
                            }
                        }

                        SystemBarColorIcons(
                            darkIcons = true
                        )
                        ScreenContent()
                    }
                }
            }
        }
    }

    @Composable
    private fun SystemBarColorIcons(darkIcons: Boolean) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = darkIcons
                }
            }
        }
    }

    @Composable
    abstract fun ScreenContent()
}