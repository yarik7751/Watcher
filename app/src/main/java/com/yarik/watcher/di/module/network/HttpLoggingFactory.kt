package com.yarik.watcher.di.module.network

import com.yarik.watcher.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

object HttpLoggingConfig {

    const val DEFAULT_TAG = "WatcherHttp"

    val isEnabled: Boolean
        get() = BuildConfig.DEBUG || BuildConfig.ENABLE_HTTP_LOGS
}

object HttpLoggingFactory {

    fun create(tag: String = HttpLoggingConfig.DEFAULT_TAG): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            android.util.Log.d(tag, sanitizeLogMessage(message))
        }.apply {
            level = if (HttpLoggingConfig.isEnabled) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
        }
    }

    private fun sanitizeLogMessage(message: String): String {
        val withoutBase64 = BASE64_DATA_URL.replace(message) { match ->
            val payload = match.value
            "data:image/...;base64,[${payload.length} chars omitted]"
        }
        return if (withoutBase64.length <= MAX_LOG_LENGTH) {
            withoutBase64
        } else {
            withoutBase64.take(MAX_LOG_LENGTH) + "... [truncated]"
        }
    }

    private val BASE64_DATA_URL = Regex("""data:image/[^;]+;base64,[A-Za-z0-9+/=\s]+""")

    private const val MAX_LOG_LENGTH = 4_000
}
