package tech.jorgecastro.jsonapi.example

import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.example.dto.Article
import tech.jorgecastro.jsonapi.example.dto.ZoneCoverage

interface TestApi {

    @JsonApiMethod
    @GET("v2/5e538c4b2e000058002dac0d")
    suspend fun getData1(): List<ZoneCoverage>

    @JsonApiMethod
    @GET("v2/5e538c4b2e000058002dac0d")
    fun getDataWithFlow(): Flow<List<ZoneCoverage>>

    @JsonApiMethod
    @GET("v2/5e8110583000002c006f964e")
    suspend fun getOneData(): ZoneCoverage

    @JsonApiMethod
    @GET("v2/5e8110583000002c006f964e")
    suspend fun getOneDataWithFlow(): Flow<ZoneCoverage>


    @JsonApiMethod
    @GET("v2/5e538c4b2e000058002dac0d")
    fun getDataObservableList(): Observable<List<ZoneCoverage>>

    @JsonApiMethod
    @GET("v2/5e538c4b2e000058002dac0d")
    fun getDataSingleList(): Single<List<ZoneCoverage>>


    @JsonApiMethod
    @GET("v2/5e797e302d0000ab7b18bd39")
    fun getErrorDataWithFlow(): Flow<List<ZoneCoverage>>


    @JsonApiMethod
    @GET("v2/5e797e302d0000ab7b18bd39")
    fun getErrorDataWithRxJava(): Observable<List<ZoneCoverage>>


    // Relationship
    @JsonApiMethod
    @GET("https://api.myjson.com/bins/15to00")
    fun getArticles(): Single<List<Article>>

    // Relationship
    @JsonApiMethod
    @GET("v2/5e7ffbad2f00003f57bac5a1")
    fun getArticlesWithMultipleAuthors(): Single<List<Article>>
}