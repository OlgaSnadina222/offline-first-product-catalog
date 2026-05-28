package com.example.app_retrofit2.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // REQUEST
        val requestBody = request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        }
        Log.d(
            "API_REQUEST",
            """
            ➡️ ${request.method} ${request.url}
            Headers: ${request.headers}
            Body: ${requestBody ?: "empty"}
            """.trimIndent()
        )

        val start = System.nanoTime()
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Network error: ${e.message}")
            throw e
        }

        val tookMs = (System.nanoTime() - start) / 1_000_000

        // RESPONSE
        val rawBody = response.body.string()
        val logTag = if (response.code in 400..599) "API_ERROR" else "API_RESPONSE"

        Log.println(
            if (response.code in 400..599) Log.ERROR else Log.DEBUG,
            logTag,
            """
            ⬅️ ${response.code} ${response.message}
            URL: ${request.url}
            Time: ${tookMs}ms
            Body: ${rawBody ?: "empty"}
            """.trimIndent()
        )
        return rawBody.toResponseBody(response.body.contentType()).let {
            response.newBuilder()
                .body(it)
        }
            .build()
    }
}