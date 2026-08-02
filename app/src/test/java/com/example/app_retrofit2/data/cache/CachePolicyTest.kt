package com.example.app_retrofit2.data.cache

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CachePolicyTest {
    private val timeInMinutes = 5
    private val expectedTimeout = timeInMinutes * 60 * 1000L

    @Test
    fun cache_should_not_be_expired_when_updated_recently() {
        val updatedAt = System.currentTimeMillis()
        val result = CachePolicy.isCacheExpired(updatedAt)
        assertFalse(result)
    }

    @Test
    fun cache_should_be_expired_when_timeout_exceeded() {
        val updatedAt = System.currentTimeMillis() - CachePolicy.DEFAULT_TIMEOUT - 1
        val result = CachePolicy.isCacheExpired(updatedAt)
        assertTrue(result)
    }

    @Test
    fun cache_should_respect_custom_timeout() {
        val updatedAt = System.currentTimeMillis() - 3000
        val result = CachePolicy.isCacheExpired(
            updatedAt = updatedAt,
            timeout = 5000
        )
        assertFalse(result)
    }

    @Test
    fun cache_should_expire_exactly_after_custom_timeout() {
        val updatedAt = System.currentTimeMillis() - 6000
        assertTrue(
            CachePolicy.isCacheExpired(
                updatedAt,
                timeout = 5000
            )
        )
    }

    @Test
    fun default_timeout_should_be_timeInMinutes_minutes() {
        assertEquals(expectedTimeout, CachePolicy.DEFAULT_TIMEOUT)
    }
}