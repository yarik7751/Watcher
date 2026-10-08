package com.yarik.watcher.feature.masterpro.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

/**
 * Карточка заявки. Аргумент — jobId — передаётся query-параметром:
 * конкретный URL для навигации строится через [create], а destination
 * регистрируется по [ROUTE_PATTERN] (так WatcherNavHost может поднять navArgument).
 */
class JobCardJoyScreen private constructor(val jobId: Long) : JoyScreen<NoArgs> {

    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = "$BASE?$ARG_JOB_ID=$jobId"

    companion object {
        const val BASE = "masterpro_job_card"
        const val ROUTE_PATTERN = "masterpro_job_card?jobId={jobId}"
        const val ARG_JOB_ID = "jobId"

        fun create(jobId: Long) = JobCardJoyScreen(jobId)
    }
}
