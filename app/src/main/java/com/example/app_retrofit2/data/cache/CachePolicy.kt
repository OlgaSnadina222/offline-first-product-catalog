package com.example.app_retrofit2.data.cache

object CachePolicy {
    const val TIME_IN_MINUTES = 5
    const val DEFAULT_TIMEOUT = TIME_IN_MINUTES * 60 * 1000L
    fun isCacheExpired(updatedAt: Long, timeout: Long = DEFAULT_TIMEOUT): Boolean {
        return System.currentTimeMillis() - updatedAt > timeout
    }
}