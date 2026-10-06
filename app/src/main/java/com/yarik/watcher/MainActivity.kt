package com.yarik.watcher

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.yarik.watcher.application.WatcherApplication
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.snackbar.flow.SnackBarManagerFlow
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var router: JoyRouter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var screenContentProviders: Map<String, @JvmSuppressWildcards ScreenContentProvider>

    @Inject
    lateinit var snackBarManagerFlow: SnackBarManagerFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        WatcherApplication.INSTANCE.appComponent.inject(this)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        initFirebase()

        setContent {
            WatcherAppContent(
                router = router,
                viewModelFactory = viewModelFactory,
                contentProviders = screenContentProviders,
                snackBarManagerFlow = snackBarManagerFlow,
            )
        }
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
}
