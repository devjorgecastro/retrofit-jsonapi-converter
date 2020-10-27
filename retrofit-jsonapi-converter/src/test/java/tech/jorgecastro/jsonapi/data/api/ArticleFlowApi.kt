package tech.jorgecastro.jsonapi.data.api

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.data.dto.Article

interface ArticleFlowApi {

    @GET("/test")
    suspend fun getOneArticleWithMultipleRelationshipNoJsonApi(): Flow<Article>

    @JsonApiMethod
    @GET("/test")
    fun getOneArticleWithMultipleRelationship(): Flow<Article>

    @JsonApiMethod
    @GET("/test")
    suspend fun getArticles(): Flow<List<Article>>
}
