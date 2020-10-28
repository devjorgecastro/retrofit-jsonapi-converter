package tech.jorgecastro.jsonapi.data.api

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import tech.jorgecastro.jsonapi.JsonApiMethod
import tech.jorgecastro.jsonapi.data.dto.Article

interface ArticleApi {

    @JsonApiMethod
    @GET("/test")
    suspend fun getOneArticleWithMultipleRelationship(): Article

    @JsonApiMethod
    @GET("/test")
    suspend fun getArticles(): List<Article>

    @JsonApiMethod
    @GET("/test")
    suspend fun getArticlesWithFlow(): Flow<List<Article>>
}
