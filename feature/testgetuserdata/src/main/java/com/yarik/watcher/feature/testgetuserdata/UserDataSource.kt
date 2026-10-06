package com.yarik.watcher.feature.testgetuserdata.datasource

import java.util.Random

object UserDataSource {

    private val mockUsers = listOf(
        BestUser(1, "John", "Doe", 28, "+1 (555) 019-2834"),
        BestUser(2, "Jane", "Smith", 24, "+1 (555) 014-9281"),
        BestUser(3, "Michael", "Johnson", 35, "+1 (555) 017-3849"),
        BestUser(4, "Emily", "Davis", 31, "+1 (555) 012-4756"),
        BestUser(5, "David", "Miller", 42, "+1 (555) 015-2938"),
        BestUser(6, "Sarah", "Wilson", 29, "+1 (555) 011-8472"),
        BestUser(7, "James", "Taylor", 45, "+1 (555) 016-5923"),
        BestUser(8, "Amanda", "Anderson", 27, "+1 (555) 013-1049"),
        BestUser(9, "Robert", "Thomas", 38, "+1 (555) 018-4627"),
        BestUser(10, "Jessica", "Jackson", 33, "+1 (555) 019-5731"),
        BestUser(11, "William", "White", 50, "+1 (555) 014-2398"),
        BestUser(12, "Ashley", "Harris", 26, "+1 (555) 012-8374"),
        BestUser(13, "Brian", "Martin", 30, "+1 (555) 017-1155"),
        BestUser(14, "Megan", "Thompson", 23, "+1 (555) 015-6677"),
        BestUser(15, "Kevin", "Garcia", 41, "+1 (555) 011-9988")
    )

    private val random = Random()

    fun getBestUserOfDay(): BestUser {
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        val randomIndex = random.nextInt(mockUsers.size)
        return mockUsers[randomIndex]
    }
}
