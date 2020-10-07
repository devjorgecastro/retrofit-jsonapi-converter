package tech.jorgecastro.jsonapi.example

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.example.dto.Article

interface ArticlesApi {
    @GET("https://run.mocky.io/v3/24a0bf33-a254-473c-8a01-933cfbe207ab")
    fun getArticlesWithRxJava(): Observable<List<Article>>


    @GET("https://run.mocky.io/v3/24a0bf33-a254-473c-8a01-933cfbe207ab")
    suspend fun getArticlesWithCoroutine(): Flow<List<Article>>
}