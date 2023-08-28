package com.example.fetchlist

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NetworkUnitTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: OkHttpClient
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        client = OkHttpClient.Builder().build()

        mockWebServer.start()
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun unsuccessful_response() {

        val mockResponse = MockResponse().setResponseCode(200).setBody(null)

    }
}