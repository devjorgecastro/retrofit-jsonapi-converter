package tech.jorgecastro.jsonapi.dto

import com.squareup.moshi.Json
import tech.jorgecastro.jsonapi.JsonApiResource


@JsonApiResource(name = "zone_coverage")
data class ZoneCoverage(
    @field:Json(name = "id") var id: String = "",
    @Json(name = "country_name") var countryName: String,
    @Json(name = "city_name") var cityName: String
)