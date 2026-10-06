package com.yarik.watcher.features.home.semaphore

import com.yarik.watcher.features.home.base.ExampleBase

object SemaphoreExample : ExampleBase() {

    private val uploadLimiter = UploadLimiter()

    override fun invoke() {
        val th1 = Thread { uploadLimiter.upload(TestFile(), { log("uploaded") }) }
        val th2 = Thread { uploadLimiter.upload(TestFile(), { log("uploaded") }) }
        val th3 = Thread { uploadLimiter.upload(TestFile(), { log("uploaded") }) }
        val th4 = Thread { uploadLimiter.upload(TestFile(), { log("uploaded") }) }
        val th5 = Thread { uploadLimiter.upload(TestFile(), { log("uploaded") }) }
        val th6 = Thread { uploadLimiter.upload(TestFile(), { log("uploaded") }) }
        val th7 = Thread { uploadLimiter.upload(TestFile(), { log("uploaded") }) }
        val th8 = Thread { uploadLimiter.upload(TestFile(), { log("uploaded") }) }
        th1.start()
        th2.start()
        th3.start()
        th4.start()
        th5.start()
        th6.start()
        th7.start()
        th8.start()
    }
}