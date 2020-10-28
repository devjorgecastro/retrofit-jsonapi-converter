package tech.jorgecastro.jsonapi

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.adapter.JsonApiCallAdapterFactory
import tech.jorgecastro.jsonapi.converter.JsonApiConverterFactory
import tech.jorgecastro.jsonapi.data.ArticleMockData
import tech.jorgecastro.jsonapi.examples.api.TestFlowApi
import java.lang.Exception
import java.net.HttpURLConnection

class JsonApiConfigurationTest {

    private val mockWebServer = MockWebServer()
    private lateinit var apiService: TestFlowApi

    @Before
    fun setup() {
        mockWebServer.start()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test if JsonApiCallAdapterFactory is configured`() = runBlocking {

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(JsonApiConverterFactory())
            .client(OkHttpClient())
            .build()
            .create(TestFlowApi::class.java)


        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(ArticleMockData().ARTICLE_DATA_LIST)

        mockWebServer.enqueue(response)


        try {
            apiService.test()
                .collect()

        } catch (e: Exception) {
            assertTrue(
                e.message?.contains("Unable to create call adapter for kotlinx.coroutines.flow.Flow")
                    ?: false
            )
        }
    }


    @Test
    fun `test if JsonApiConverterFactory is configured`() = runBlocking {

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addCallAdapterFactory(JsonApiCallAdapterFactory.create())
            .client(OkHttpClient())
            .build()
            .create(TestFlowApi::class.java)


        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(ArticleMockData().ARTICLE_DATA_LIST)

        mockWebServer.enqueue(response)


        try {
            apiService.test()
                .collect()

        } catch (e: Exception) {
            assertEquals(
                e.message,
                "Unable to create converter for java.util.List<java.lang.String>\n" +
                        "    for method TestFlowApi.test"
            )
        }
    }
}