package com.yarik.watcher.feature.home.impl.simplecounter

import com.yarik.watcher.feature.home.impl.base.ExampleBase

object SimpleCounter : ExampleBase() {
    private var counter: Int = 0
    private val monitor = Any()

    override fun invoke() = withTime {
        counter = 0
        val th1 = Thread {
            synchronized(monitor) {
                repeat(100000) {
                    counter++
                }
            }
        }

        val th2 = Thread {
            synchronized(monitor) {
                repeat(100000) {
                    counter++
                }
            }
        }

        th1.start()
        th2.start()

        th1.join()
        th2.join()

        log("counter -> $counter")
    }
}