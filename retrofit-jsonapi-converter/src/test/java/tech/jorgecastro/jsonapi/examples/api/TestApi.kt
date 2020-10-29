package tech.jorgecastro.jsonapi.examples.api

import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.data.dto.ZoneCoverage

interface TestSingleApi {
    @JsonApiMethod
    @GET("https://run.mocky.io/v3/44db10b9-9aea-4c53-aa3e-d2ef892fa859")
    fun test(): Single<List<String>>
}

interface TestObservableApi {
    @JsonApiMethod
    @GET("https://run.mocky.io/v3/44db10b9-9aea-4c53-aa3e-d2ef892fa859")
    fun test(): Observable<List<String>>
}

interface TestFlowApi {
    @JsonApiMethod
    @GET("https://run.mocky.io/v3/44db10b9-9aea-4c53-aa3e-d2ef892fa859")
    fun test(): Flow<List<String>>
}
