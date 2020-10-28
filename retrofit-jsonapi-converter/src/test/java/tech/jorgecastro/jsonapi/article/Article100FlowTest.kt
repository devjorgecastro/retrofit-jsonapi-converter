package tech.jorgecastro.jsonapi.article

import junit.framework.TestCase.assertEquals
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
import tech.jorgecastro.jsonapi.common.ArticlesMockJson
import tech.jorgecastro.jsonapi.common.CoroutineTestRule
import tech.jorgecastro.jsonapi.converter.JsonApiConverterFactory
import tech.jorgecastro.jsonapi.data.api.ArticleApi
import java.net.HttpURLConnection

class Article100FlowTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val mockWebServer = MockWebServer()
    private lateinit var apiService: ArticleApi
    private var articlesMockJson: ArticlesMockJson? = ArticlesMockJson()

    @Before
    fun setup() {
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(JsonApiConverterFactory())
            .addCallAdapterFactory(JsonApiCallAdapterFactory.create())
            .client(OkHttpClient())
            .build()
            .create(ArticleApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()

        articlesMockJson = null
    }

    @Test
    fun `test getArticles with 100 rows - success`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(articlesMockJson?.getMockJson() ?: "")
        mockWebServer.enqueue(response)

        apiService.getArticlesWithFlow()
            .collect { articles ->

                assertEquals(articles.count(), 100)

                val firstArticle = articles.first()
                assertEquals(firstArticle.title, "JSON:API paints my bikeshed! - ID=1")
                assertEquals(firstArticle.authors?.count(), 5)
                assertEquals(
                    firstArticle.authors?.map { it.id },
                    listOf("41", "42", "43", "44", "45")
                )


                val articleWithId50 = articles.first { it.id == "50" }
                assertEquals(articleWithId50.title, "JSON:API paints my bikeshed! - ID=50")
                assertEquals(articleWithId50.authors?.count(), 3)
                assertEquals(articleWithId50.authors?.map { it.id }, listOf("41", "43", "45"))
                assertEquals(
                    articleWithId50.authors?.first { it.id == "43" }?.name,
                    "Martin C. Robert"
                )


                val lastArticle = articles.last()
                assertEquals(lastArticle.title, "JSON:API paints my bikeshed! - ID=100")
                assertEquals(lastArticle.authors?.count(), 4)
                assertEquals(lastArticle.authors?.map { it.id }, listOf("41", "42", "44", "45"))
            }

    }

    @Test
    fun `test getArticles with 5000 rows - success`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(articlesMockJson?.getMockJson(5000) ?: "")
        mockWebServer.enqueue(response)

        apiService.getArticlesWithFlow()
            .collect { articles ->

                assertEquals(articles.count(), 5000)

                val firstArticle = articles.first()
                assertEquals(firstArticle.title, "JSON:API paints my bikeshed! - ID=1")
                assertEquals(firstArticle.authors?.count(), 5)
                assertEquals(
                    firstArticle.authors?.map { it.id },
                    listOf("41", "42", "43", "44", "45")
                )


                val articleWithId500 = articles.first { it.id == "500" }
                assertEquals(articleWithId500.title, "JSON:API paints my bikeshed! - ID=500")
                assertEquals(articleWithId500.authors?.count(), 3)
                assertEquals(articleWithId500.authors?.map { it.id }, listOf("41", "43", "45"))
                assertEquals(
                    articleWithId500.authors?.first { it.id == "43" }?.name,
                    "Martin C. Robert"
                )


                val lastArticle = articles.last()
                assertEquals(lastArticle.title, "JSON:API paints my bikeshed! - ID=5000")
                assertEquals(lastArticle.authors?.count(), 4)
                assertEquals(lastArticle.authors?.map { it.id }, listOf("41", "42", "44", "45"))
            }

    }

    @Test
    fun `test getArticles with 10000 rows - success`() = runBlocking {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(articlesMockJson?.getMockJson(10000) ?: "")
        mockWebServer.enqueue(response)

        apiService.getArticlesWithFlow()
            .collect { articles ->

                assertEquals(articles.count(), 10000)

                val firstArticle = articles.first()
                assertEquals(firstArticle.title, "JSON:API paints my bikeshed! - ID=1")
                assertEquals(firstArticle.authors?.count(), 5)
                assertEquals(
                    firstArticle.authors?.map { it.id },
                    listOf("41", "42", "43", "44", "45")
                )


                val articleWithId500 = articles.first { it.id == "5000" }
                assertEquals(articleWithId500.title, "JSON:API paints my bikeshed! - ID=5000")
                assertEquals(articleWithId500.authors?.count(), 3)
                assertEquals(articleWithId500.authors?.map { it.id }, listOf("41", "43", "45"))
                assertEquals(
                    articleWithId500.authors?.first { it.id == "43" }?.name,
                    "Martin C. Robert"
                )


                val lastArticle = articles.last()
                assertEquals(lastArticle.title, "JSON:API paints my bikeshed! - ID=10000")
                assertEquals(lastArticle.authors?.count(), 4)
                assertEquals(lastArticle.authors?.map { it.id }, listOf("41", "42", "44", "45"))
            }

    }
}