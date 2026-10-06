package com.yarik.watcher.features.testgetuserdata.data

import android.os.Handler
import android.os.Looper
import com.yarik.watcher.features.testgetuserdata.datasource.BestUser
import com.yarik.watcher.features.testgetuserdata.datasource.UserDataSource
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

class UserRepository {

    @Volatile
    var cache: BestUser? = null

    private var _callbacks: CopyOnWriteArrayList<WeakReference<Result>> = CopyOnWriteArrayList()

    private val uiHandler = Handler(Looper.getMainLooper())

    private val lock = Any()

    fun subscribe(callback: Result) {
        val isAlreadySubscribed = _callbacks.any { it.get() === callback }

        if (!isAlreadySubscribed) {
            _callbacks.add(WeakReference(callback))
        }

        cache?.let {
            sendUser(
                callback = callback,
                user = it
            )
        }
    }

    fun unsubscribe(callback: Result) {
        _callbacks.removeAll { ref ->
            ref.get() === callback || ref.get() == null
        }
    }

    fun loadBestUserOfDay() {
        thread {
            cache?.let {
                sendUser(it)
                return@thread
            }

            synchronized(lock) {
                cache?.let {
                    sendUser(it)
                    return@thread
                }

                val newUser = UserDataSource.getBestUserOfDay()
                cache = newUser
                sendUser(newUser)
            }
        }
    }

    fun reloadData() {
        cache = null
        loadBestUserOfDay()
    }

    private fun sendUser(
        user: BestUser,
        callback: Result? = null,
    ) {
        callback?.let {
            uiHandler.post {
                it.onReceive(user)
            }
        } ?: run {
            _callbacks.forEach {
                uiHandler.post {
                    it.get()?.onReceive(user)
                }
            }
        }
    }

    interface Result {
        fun onReceive(user: BestUser)
    }
}