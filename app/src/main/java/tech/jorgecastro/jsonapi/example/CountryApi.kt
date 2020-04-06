package tech.jorgecastro.jsonapi.example

import com.squareup.moshi.Json
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.JsonApiResource

interface CountryApi {
    @JsonApiMethod
    @GET("v2/5e3f90bc3300006300b04bf3")
    suspend fun getCountryCodes(): Flow<List<CountryCode>>
}

@JsonApiResource(name = "country")
data class CountryCode(
    @field:Json(name = "id") var id: String = "",
    @Json(name = "code") var code: Int,
    @Json(name = "code_iso_2") var codeIso2: String = "",
    @Json(name = "code_iso_3") var codeIso3: String = "",
    @Json(name = "name") var name: String,
    @Json(name = "flag") var flag: String
)