package tech.jorgecastro.jsonapi.example

import com.squareup.moshi.Json
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.JsonApiRelationship
import tech.jorgecastro.jsonapi.JsonApiResource
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




    @JsonApiMethod
    @GET("https://run.mocky.io/v3/44db10b9-9aea-4c53-aa3e-d2ef892fa859")
    fun test(): Single<List<Address>>
}


@JsonApiResource(type = "Address")
data class Address(
    @field:Json(name = "id") val id: String = "",
    val label: String = "",
    val address: String = "",
    @Json(name = "address_further")
    val addressFurther: String = "",
    val neighborhood: String = "",

    @JsonApiRelationship(jsonApiResourceName = "Country", jsonAttrName = "Country")
    val country: Country? = null,

    @JsonApiRelationship(jsonApiResourceName = "City", jsonAttrName = "City")
    val city: City? = null
)

@JsonApiResource(type = "Country")
data class Country(
    @field:Json(name = "id") val id: String = "",
    val name: String,
    val code: String,
    val language: String,
    @Json(name = "url_domain") val urlDomain: String,
    @Json(name = "decimal_amount") val decimalAmount: Int,
    @Json(name = "url_country_flag") val urlCountryFlag: String,
    @Json(name = "phone_code") val phoneCode: String
)

@JsonApiResource(type = "City")
data class City(
    @field:Json(name = "id") val id: String = "",
    val city: String,
    val slug: String,
    @Json(name = "address_type") val addressType: String
)