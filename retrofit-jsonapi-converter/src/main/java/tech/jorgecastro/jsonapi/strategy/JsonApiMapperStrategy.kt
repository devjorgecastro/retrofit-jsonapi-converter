package tech.jorgecastro.jsonapi.strategy

import tech.jorgecastro.jsonapi.JsonApiMapper
import tech.jorgecastro.jsonapi.JsonApiResponse
import kotlin.reflect.KClass

interface JsonApiMapperStrategy {
    fun map(
        jsonApiResponse: JsonApiResponse,
        outputObjectRawType: KClass<*>,
        jsonApiMapper: JsonApiMapper
    ): Any?
}
