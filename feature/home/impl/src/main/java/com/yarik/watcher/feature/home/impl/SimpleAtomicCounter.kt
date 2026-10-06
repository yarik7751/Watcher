package com.yarik.watcher.feature.home.impl.simplecounter

import com.yarik.watcher.feature.home.impl.base.ExampleBase
import java.util.concurrent.atomic.AtomicInteger

object SimpleAtomicCounter : ExampleBase() {
    private val counter = AtomicInteger(0)

    override fun invoke() = withTime {
        counter.set(0)
        val th1 = Thread {
            repeat(100000) {
                counter.incrementAndGet()
            }
        }

        val th2 = Thread {
            repeat(100000) {
                counter.incrementAndGet()
            }
        }

        th1.start()
        th2.start()

        th1.join()
        th2.join()

        log("counter -> ${counter.get()}")
    }
}
