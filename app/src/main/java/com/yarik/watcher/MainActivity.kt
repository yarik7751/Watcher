package com.yarik.watcher

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.yarik.watcher.snackbar.ShackBar
import com.yarik.watcher.application.WatcherApplication
import com.yarik.watcher.features.start.StartJoyScreen
import com.yarik.watcher.navigation.router.JoyRouter
import com.yarik.watcher.snackbar.flow.SnackBarManagerFlow
import com.yarik.watcher.snackbar.model.SnackBarData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val SNACK_BAR_ANIMATION_DURATION = 300

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var router: JoyRouter

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val initNavigator = AppNavigator(this, R.id.initContainer)

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    @Inject
    lateinit var snackBarManagerFlow: SnackBarManagerFlow
    override fun onCreate(savedInstanceState: Bundle?) {
        WatcherApplication.INSTANCE.appComponent.inject(this)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        initFirebase()
        backActionHandling()

        setContentView(R.layout.activity_main)

        router.replaceScreen(StartJoyScreen)

        initSnackBar()
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            token.hashCode()
            // TODO send token to server
        })
    }

    private fun backActionHandling() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.fragments.isNotEmpty()) {
                    router.exit()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initSnackBar() {
        findViewById<ComposeView>(R.id.shackBar).apply {
            this.setContent {
                var snackBarData by remember { mutableStateOf<SnackBarData?>(null) }
                LaunchedEffect(Unit) {
                    snackBarManagerFlow.observe().onEach { data ->
                        snackBarData = data
                        delay(data.duration.durationMs)
                        snackBarData = null
                    }.launchIn(this)
                }

                AnimatedContent(
                    snackBarData != null,
                    transitionSpec = {
                        ContentTransform(
                            targetContentEnter = fadeIn(
                                animationSpec = tween(SNACK_BAR_ANIMATION_DURATION),
                            ),
                            initialContentExit = fadeOut(
                                animationSpec = tween(SNACK_BAR_ANIMATION_DURATION),
                            ),
                            sizeTransform = SizeTransform(
                                sizeAnimationSpec = { _, _ -> tween(SNACK_BAR_ANIMATION_DURATION) },
                            )
                        )
                    }

                ) { targetState ->
                    if (targetState) {
                        snackBarData?.let {
                            ShackBar(
                                data = it,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(initNavigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        onBackPressedCallback.remove()
        super.onDestroy()
    }
}