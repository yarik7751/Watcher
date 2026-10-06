package com.yarik.watcher.feature.home.impl.semaphore

import java.util.concurrent.Semaphore

class UploadLimiter(maxParallel: Int = 3) {

    private val permits = Semaphore(maxParallel)

    fun upload(file: TestFile, done: () -> Unit) {
        permits.acquire()
        try {
            file.upload(done)
        } finally {
            permits.release() // ← ОБЯЗАТЕЛЬНО в finally
        }
    }
}

class TestFile {
    fun upload(done: () -> Unit) {
        Thread.sleep(1000)
        done()
    }
}