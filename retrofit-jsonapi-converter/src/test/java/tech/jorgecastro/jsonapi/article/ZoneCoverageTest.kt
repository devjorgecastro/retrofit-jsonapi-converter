package tech.jorgecastro.jsonapi.article

import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
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
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.adapter.JsonApiCallAdapterFactory
import tech.jorgecastro.jsonapi.common.CoroutineTestRule
import tech.jorgecastro.jsonapi.common.MockJson
import tech.jorgecastro.jsonapi.converter.JsonApiConverterFactory
import tech.jorgecastro.jsonapi.data.api.ZoneCoverageApi
import tech.jorgecastro.jsonapi.data.dto.ZoneCoverage
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class ZoneCoverageTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()


    private val jsonNameList = "zonecoverage/zone-coverage-without-relationship"
    private val jsonNameObject = "zonecoverage/zonecoverage-single-no-relationship"

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

    /**
     * Flow
     */
    @Test
    fun `test getZoneCoverageWithFlow with is success`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonNameList) ?: "")
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
            .setBody(mockJson?.getMockJson(jsonNameList) ?: "")
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

    @Test
    fun `test getSingleZoneCoverageWithFlow when response is success`() = runBlocking {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonNameObject) ?: "")
        mockWebServer.enqueue(response)

        apiService.getSingleZoneCoverageWithFlow()
            .collect {
                assertEquals(it.id, "05429593-ca29-4c41-92b6-3eeed1d063cd")
                assertEquals(it.countryName, "Colombia")
                assertEquals(it.cityName, "Bogotá, D.C.")
            }
    }


    /**
     * RxJava Observable
     */
    @Test
    fun `test getZoneCoverageWithObservable with is success`() {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonNameList) ?: "")
        mockWebServer.enqueue(response)

        val testObserver = apiService.getZoneCoverageWithObservable()
            .test()
        testObserver.awaitDone(3, TimeUnit.SECONDS)
            .assertComplete()
            .assertValueCount(1)
            .assertValue { it.size == 2 }

        testObserver
            .assertValue { it.first().id == "05429593-ca29-4c41-92b6-3eeed1d063cd" }
            .assertValue { it.first().countryName == "Colombia" }
            .assertValue { it.first().cityName == "Bogotá, D.C." }

        testObserver
            .assertValue { it.last().id == "05429593-ca29-4c41-92b6-3eeed1d063ce" }
            .assertValue { it.last().countryName == "Chile" }
            .assertValue { it.last().cityName == "Santiago de Chile" }

        testObserver.dispose()
    }

    @Test
    fun `test getSingleZoneCoverageWithObservable with is success`() {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonNameObject) ?: "")
        mockWebServer.enqueue(response)

        val observer = TestObserver<ZoneCoverage>()
        apiService.getSingleZoneCoverageWithObservable()
            .subscribeOn(Schedulers.computation())
            .subscribe(observer)

        observer.awaitTerminalEvent()
        observer.assertValue { it.id == "05429593-ca29-4c41-92b6-3eeed1d063cd" }
        observer.assertValue { it.countryName == "Colombia" }
        observer.assertValue { it.cityName == "Bogotá, D.C." }

        observer.dispose()
    }

    @Test
    fun `test getSingleZoneCoverageWithObservable when expected BEGIN_OBJECT`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonNameList) ?: "")
        mockWebServer.enqueue(response)


        val observer = TestObserver<ZoneCoverage>()
        apiService.getSingleZoneCoverageWithObservable()
            .subscribe(observer)

        observer.awaitTerminalEvent()
        observer.assertErrorMessage("Expected BEGIN_OBJECT but was BEGIN_ARRAY at path \$.data")

        observer.dispose()
    }


    /**
     * RxJava Single
     */
    @Test
    fun `test getZoneCoverageWithSingle with is success`() {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonNameList) ?: "")
        mockWebServer.enqueue(response)

        val testObserver = apiService.getZoneCoverageWithSingle()
            .test()
        testObserver.awaitDone(3, TimeUnit.SECONDS)
            .assertComplete()
            .assertValueCount(1)
            .assertValue { it.size == 2 }

        testObserver
            .assertValue { it.first().id == "05429593-ca29-4c41-92b6-3eeed1d063cd" }
            .assertValue { it.first().countryName == "Colombia" }
            .assertValue { it.first().cityName == "Bogotá, D.C." }

        testObserver
            .assertValue { it.last().id == "05429593-ca29-4c41-92b6-3eeed1d063ce" }
            .assertValue { it.last().countryName == "Chile" }
            .assertValue { it.last().cityName == "Santiago de Chile" }

        testObserver.dispose()
    }

    @Test
    fun `test getSingleZoneCoverageWithSingle with is success`() {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonNameObject) ?: "")
        mockWebServer.enqueue(response)

        val observer = TestObserver<ZoneCoverage>()
        apiService.getSingleZoneCoverageWithSingle()
            .subscribeOn(Schedulers.computation())
            .subscribe(observer)

        observer.awaitTerminalEvent()
        observer.assertValue { it.id == "05429593-ca29-4c41-92b6-3eeed1d063cd" }
        observer.assertValue { it.countryName == "Colombia" }
        observer.assertValue { it.cityName == "Bogotá, D.C." }

        observer.dispose()
    }

    @Test
    fun `test getSingleZoneCoverageWithSingle when expected BEGIN_OBJECT`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockJson?.getMockJson(jsonNameList) ?: "")
        mockWebServer.enqueue(response)


        val observer = TestObserver<ZoneCoverage>()
        apiService.getSingleZoneCoverageWithSingle()
            .subscribe(observer)

        observer.awaitTerminalEvent()
        observer.assertErrorMessage("Expected BEGIN_OBJECT but was BEGIN_ARRAY at path \$.data")

        observer.dispose()
    }
}