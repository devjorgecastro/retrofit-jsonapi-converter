package tech.jorgecastro.jsonapi.data.dto

import com.squareup.moshi.Json
import tech.jorgecastro.jsonapi.JsonApiRelationship
import tech.jorgecastro.jsonapi.JsonApiResource

@JsonApiResource(name = "people")
data class People(
    @field:Json(name = "id") var id: String = "",
    var name: String = "",
    var age: String = "",
    var gender: String = ""
)

@JsonApiResource(name = "articles")
data class Article(
    @field:Json(name = "id") var id: String = "",
    var title: String = "",
    var body: String = "",
    var created: String = "",
    var updated: String = "",
    @JsonApiRelationship(jsonApiResourceName = "people", jsonAttrName = "author") var authors: List<People>? = listOf()
)