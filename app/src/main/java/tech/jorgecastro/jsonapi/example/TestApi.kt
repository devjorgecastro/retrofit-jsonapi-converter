package tech.jorgecastro.jsonapi.example

import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.dto.ZoneCoverage

interface TestApi {
    @GET("v2/5e538c4b2e000058002dac0d")
    suspend fun getData1(): List<ZoneCoverage>

    @GET("v2/5e538c4b2e000058002dac0d")
    suspend fun getData2(): Flow<List<ZoneCoverage>>


    @JsonApiMethod
    @GET("v2/5e538c4b2e000058002dac0d")
    fun getDataObservableList(): Observable<List<ZoneCoverage>>

    @JsonApiMethod
    @GET("v2/5e538c4b2e000058002dac0d")
    fun getDataSingleList(): Single<List<ZoneCoverage>>

    @GET("v2/5e797e302d0000ab7b18bd39")
    suspend fun getDataWithError1(): List<ZoneCoverage>
}