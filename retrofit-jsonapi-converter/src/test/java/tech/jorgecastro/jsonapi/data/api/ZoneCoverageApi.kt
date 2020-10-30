package tech.jorgecastro.jsonapi.data.api

import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.data.dto.ZoneCoverage

interface ZoneCoverageApi {

    @JsonApiMethod
    @GET("/")
    fun getZoneCoverageWithFlow(): Flow<List<ZoneCoverage>>

    @JsonApiMethod
    @GET("/")
    fun getSingleZoneCoverageWithFlow(): Flow<ZoneCoverage>

    @JsonApiMethod
    @GET("/")
    fun getZoneCoverageWithObservable(): Observable<List<ZoneCoverage>>

    @JsonApiMethod
    @GET("/")
    fun getSingleZoneCoverageWithObservable(): Observable<ZoneCoverage>

    @JsonApiMethod
    @GET("/")
    fun getZoneCoverageWithSingle(): Single<List<ZoneCoverage>>

    @JsonApiMethod
    @GET("/")
    fun getSingleZoneCoverageWithSingle(): Single<ZoneCoverage>
}