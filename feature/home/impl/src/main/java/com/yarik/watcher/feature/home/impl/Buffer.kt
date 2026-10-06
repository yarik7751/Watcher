package com.yarik.watcher.feature.home.impl.buffer

class Buffer(private val capacity: Int) {

    private val items = ArrayDeque<Int>()
    private val monitor = Object()

    fun put(item: Int) {
        synchronized(monitor) {
            while (items.size == capacity) {
                monitor.wait()
            }

            items.addLast(item)
            monitor.notifyAll()
        }
    }

    fun take(): Int {
        synchronized(monitor) {
            while (items.isEmpty()) {
                monitor.wait()
            }
            val first = items.removeFirst()
            monitor.notifyAll()
            return first
        }
    }

    fun clear() = items.clear()
}