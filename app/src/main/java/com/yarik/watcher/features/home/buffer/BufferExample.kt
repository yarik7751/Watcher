package com.yarik.watcher.features.home.buffer

import com.yarik.watcher.features.home.base.ExampleBase

object BufferExample : ExampleBase() {

    private val buffer = Buffer(10)

    override fun invoke() {
        buffer.clear()
        val th1 = Thread {
            repeat(1000) {
                log("put start --- $it")
                buffer.put(it)
                log("put end --- $it")
            }
        }

        val th2 = Thread {
            repeat(1000) {
                log("removed first value ---> ${buffer.take()}")
            }
        }

        th1.start()
        th2.start()

        th1.join()
        th2.join()
    }
}