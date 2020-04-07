package tech.jorgecastro.jsonapi

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.adapter.JsonApiCallAdapterFactory
import tech.jorgecastro.jsonapi.converter.JsonApiConverterFactory
import tech.jorgecastro.jsonapi.data.ArticleMockData
import tech.jorgecastro.jsonapi.data.api.ArticleFlowApi
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)
class CoroutineFlowTest {

    private val mockWebServer = MockWebServer()
    private lateinit var apiService: ArticleFlowApi
    private var mockData: ArticleMockData? = null

    @Before
    fun setup() {
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(JsonApiConverterFactory())
            .addCallAdapterFactory(JsonApiCallAdapterFactory.create())
            .client(OkHttpClient())
            .build()
            .create(ArticleFlowApi::class.java)

        mockData = ArticleMockData()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        mockData = null
    }

    /**
     * Documentation
     */
    @Test
    fun `Flow item with multiple relationship success`() = runBlocking {

        check(mockData != null) { assert(false) }

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockData!!.ARTICLE_DATA_ITEM_WITH_MULTIPLE_RELATIONSHIP)
        mockWebServer.enqueue(response)

        apiService.getOneArticleWithMultipleRelationship()
            .collect { article ->
                assert(article.id.isNotEmpty())
                assert(article.authors!!.count() > 0)
            }
    }

    @Test
    fun `suspend function with list objects success`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(ArticleMockData().ARTICLE_DATA_LIST)
        mockWebServer.enqueue(response)

        apiService.getArticles()
            .collect { articles ->
                assert(articles.count() > 0)
            }
    }
}