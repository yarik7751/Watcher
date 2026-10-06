package com.yarik.watcher.navigation.router

import android.os.Parcelable
import com.github.terrakok.cicerone.ResultListener
import com.github.terrakok.cicerone.Router
import com.yarik.watcher.navigation.JoyScreen
import com.yarik.watcher.utils.minusOneElement
import kotlinx.coroutines.flow.MutableStateFlow

class JoyRouter(val router: Router) : ScreensFlow, CurrentArgs {

    override val screensFlow = MutableStateFlow<List<JoyScreen<*>>>(emptyList())

    fun navigateTo(screen: JoyScreen<*>) {
        this@JoyRouter.router.navigateTo(screen.screen)

        screensFlow.value.plus(screen).let {
            screensFlow.tryEmit(it)
        }
    }

    fun newRootScreen(screen: JoyScreen<*>) {
        this@JoyRouter.router.newRootScreen(screen.screen)
        screensFlow.tryEmit(
            listOf(screen)
        )
    }

    fun replaceScreen(screen: JoyScreen<*>) {
        this@JoyRouter.router.replaceScreen(screen.screen)

        screensFlow.value.minusOneElement().plus(screen).let {
            screensFlow.tryEmit(it)
        }
    }

    fun backTo(screen: JoyScreen<*>?) {
        this@JoyRouter.router.backTo(screen?.screen)

        val lastIndex = screensFlow.value.lastIndexOf(screen).takeIf { it > 0 } ?: return
        screensFlow.value.subList(0, lastIndex + 1).let {
            screensFlow.tryEmit(it)
        }
    }

    fun newChain(vararg screens: JoyScreen<*>) {
        this@JoyRouter.router.newChain(*screens.map { it.screen }.toTypedArray())

        screensFlow.value.plus(screens).let {
            screensFlow.tryEmit(it)
        }
    }

    fun newRootChain(vararg screens: JoyScreen<*>) {
        this@JoyRouter.router.newRootChain(*screens.map { it.screen }.toTypedArray())

        screensFlow.value = screens.toList()
    }

    fun finishChain() {
        this@JoyRouter.router.finishChain()

        screensFlow.value = emptyList()
    }

    fun exit() {
        this@JoyRouter.router.exit()

        screensFlow.value.minusOneElement().let {
            screensFlow.tryEmit(it)
        }
    }

    fun setResultListener(
        key: String,
        listener: ResultListener
    ) = this@JoyRouter.router.setResultListener(key, listener)

    fun sendResult(key: String, data: Any) {
        this@JoyRouter.router.sendResult(key, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <A : Parcelable> get(): A {
        return screensFlow.value.last().args as A
    }
}