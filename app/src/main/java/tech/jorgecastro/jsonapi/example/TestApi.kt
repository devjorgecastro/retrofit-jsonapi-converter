package tech.jorgecastro.jsonapi.example

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiListResponse
import tech.jorgecastro.jsonapi.dto.ZoneCoverage

interface TestApi {
    @GET("v2/5e538c4b2e000058002dac0d")
    suspend fun getData1(): List<ZoneCoverage>

    @GET("v2/5e538c4b2e000058002dac0d")
    suspend fun getData2(): Flow<List<ZoneCoverage>>
}