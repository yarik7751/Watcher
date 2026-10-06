package com.yarik.watcher.feature.home.impl.reordering

import com.yarik.watcher.feature.home.impl.base.ExampleBase

object Reordering : ExampleBase() {

    @Volatile // HERE
    private var isReady = false
    private var amount = 0

    override fun invoke() = withTime {
        isReady = false
        amount = 0

        val reader = Thread {
            while (!isReady) {
                //do nothing
            }
            log("amount -> $amount")
        }

        val writer = Thread {
            isReady = true
            amount = 421
        }

        reader.start()
        Thread.sleep(20)
        writer.start()
    }
}