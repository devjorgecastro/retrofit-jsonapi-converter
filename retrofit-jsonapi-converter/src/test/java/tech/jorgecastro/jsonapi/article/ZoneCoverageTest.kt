package tech.jorgecastro.jsonapi.article

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.adapter.JsonApiCallAdapterFactory
import tech.jorgecastro.jsonapi.common.CoroutineTestRule
import tech.jorgecastro.jsonapi.common.MockJson
import tech.jorgecastro.jsonapi.converter.JsonApiConverterFactory
import tech.jorgecastro.jsonapi.data.api.ZoneCoverageApi
import java.net.HttpURLConnection

class ZoneCoverageTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val jsonName = "zone-coverage-without-relationship"
    private val mockWebServer = MockWebServer()
    private lateinit var apiService: ZoneCoverageApi
    private var mockJson: MockJson? = MockJson()

    @Before
    fun setup() {
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(JsonApiConverterFactory())
            .addCallAdapterFactory(JsonApiCallAdapterFactory.create())
            .client(OkHttpClient())
            .build()
            .create(ZoneCoverageApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        mockJson = null
    }

    @Test
    fun `test getZoneCoverageWithFlow with is success`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonName) ?: "")
        mockWebServer.enqueue(response)

        apiService.getZoneCoverageWithFlow()
            .collect { zoneCoverage ->

                assertEquals(zoneCoverage.count(), 2)

                val firstZone = zoneCoverage.first()
                assertEquals(firstZone.id, "05429593-ca29-4c41-92b6-3eeed1d063cd")
                assertEquals(firstZone.countryName, "Colombia")
                assertEquals(firstZone.cityName, "Bogotá, D.C.")

                val lastZone = zoneCoverage.last()
                assertEquals(lastZone.id, "05429593-ca29-4c41-92b6-3eeed1d063ce")
                assertEquals(lastZone.countryName, "Chile")
                assertEquals(lastZone.cityName, "Santiago de Chile")
            }

    }

    @Test
    fun `test getSingleZoneCoverageWithFlow when expected BEGIN_OBJECT`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonName) ?: "")
        mockWebServer.enqueue(response)

        apiService.getSingleZoneCoverageWithFlow()
            .catch {
                assertEquals(
                    it.message,
                    "Expected BEGIN_OBJECT but was BEGIN_ARRAY at path \$.data"
                )
            }
            .collect()

    }
}