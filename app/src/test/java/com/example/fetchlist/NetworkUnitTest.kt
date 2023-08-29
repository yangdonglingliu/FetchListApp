package com.example.fetchlist

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import java.io.IOException
import kotlin.test.assertFailsWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NetworkUnitTest {

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("Test thread")

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: OkHttpClient
    private lateinit var baseUrl:HttpUrl
    private lateinit var viewModel: MyViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {

        Dispatchers.setMain(mainThreadSurrogate)

        mockWebServer = MockWebServer()
        mockWebServer.start()

        baseUrl = mockWebServer.url("/")
        client = OkHttpClient.Builder()
            .build()

        viewModel = MyViewModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun shutdown() {
        mockWebServer.shutdown()

        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun fetch_unsuccessful_response() = runTest {

        val mockResponse = MockResponse().setResponseCode(400)
        mockWebServer.enqueue(mockResponse)

        val exception = assertFailsWith<IOException> {
            viewModel.fetchData(baseUrl)
        }

        Assert.assertEquals("The network response is not successful.", exception.message)
    }

    @Test
    fun fetch_successful_response_empty_body() = runTest {

        val mockResponse = MockResponse().setResponseCode(200).setBody("")
        mockWebServer.enqueue(mockResponse)

        val fetchedData = viewModel.fetchData(baseUrl)

        Assert.assertEquals("", fetchedData)
    }

    @Test
    fun fetch_successful_response_nonempty_body() = runTest {

        val responseBody = "[{\"id\": 755, \"listId\": 2, \"name\": \"\"},{\"id\": 203, \"listId\": 2, \"name\": \"\"},{\"id\": 684, \"listId\": 1, \"name\": \"Item 684\"},{\"id\": 276, \"listId\": 1, \"name\": \"Item 276\"},{\"id\": 736, \"listId\": 3, \"name\": null},{\"id\": 926, \"listId\": 4, \"name\": null},{\"id\": 808, \"listId\": 4, \"name\": \"Item 808\"}]"
        val mockResponse = MockResponse().setResponseCode(200).setBody(responseBody)
        mockWebServer.enqueue(mockResponse)

        val fetchedData = viewModel.fetchData(baseUrl)

        val expectedFetchedData = "[{\"id\": 755, \"listId\": 2, \"name\": \"\"},{\"id\": 203, \"listId\": 2, \"name\": \"\"},{\"id\": 684, \"listId\": 1, \"name\": \"Item 684\"},{\"id\": 276, \"listId\": 1, \"name\": \"Item 276\"},{\"id\": 736, \"listId\": 3, \"name\": null},{\"id\": 926, \"listId\": 4, \"name\": null},{\"id\": 808, \"listId\": 4, \"name\": \"Item 808\"}]"

        Assert.assertEquals(expectedFetchedData, fetchedData)
    }

}