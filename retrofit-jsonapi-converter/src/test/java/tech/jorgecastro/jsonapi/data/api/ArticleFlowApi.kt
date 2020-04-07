package tech.jorgecastro.jsonapi.data.api

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.dto.Article

interface ArticleFlowApi {

    @JsonApiMethod
    @GET("/test")
    fun getOneArticleWithMultipleRelationship(): Flow<Article>

    @JsonApiMethod
    @GET("/test")
    suspend fun getArticles(): Flow<List<Article>>
}