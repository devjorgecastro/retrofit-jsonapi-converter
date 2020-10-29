package tech.jorgecastro.jsonapi.data.api

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.data.dto.ZoneCoverage

interface ZoneCoverageApi {

    @JsonApiMethod
    @GET("/")
    fun getZoneCoverageWithFlow(): Flow<List<ZoneCoverage>>

    // Should be return error
    @JsonApiMethod
    @GET("/")
    fun getSingleZoneCoverageWithFlow(): Flow<ZoneCoverage>
}