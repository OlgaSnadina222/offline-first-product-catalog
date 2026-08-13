package com.example.app_retrofit2.data.local.room.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun fromImages_converts_list_to_json() {
        val images = listOf(
            "image1.jpg",
            "image2.jpg"
        )
        val result = converters.fromImages(images)
        assertEquals(
            "[\"image1.jpg\",\"image2.jpg\"]",
            result
        )
    }

    @Test
    fun fromImages_converts_null_to_json() {
        val result = converters.fromImages(null)
        assertEquals("null", result)
    }

    @Test
    fun toImages_converts_json_to_list() {
        val json = "[\"image1.jpg\",\"image2.jpg\"]"
        val result = converters.toImages(json)
        assertEquals(
            listOf(
                "image1.jpg",
                "image2.jpg"
            ),
            result
        )
    }

    @Test
    fun toImages_returns_empty_list_when_json_is_null() {
        val result = converters.toImages(null)
        assertTrue(result.isEmpty())
    }
}