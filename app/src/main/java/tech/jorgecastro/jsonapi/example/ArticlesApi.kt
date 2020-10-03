package tech.jorgecastro.jsonapi.example

import io.reactivex.Observable
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.dto.Article

interface ArticlesApi {
    @GET("https://run.mocky.io/v3/24a0bf33-a254-473c-8a01-933cfbe207ab")
    suspend fun getArticlesWithRxJava(): Observable<List<Article>>
}