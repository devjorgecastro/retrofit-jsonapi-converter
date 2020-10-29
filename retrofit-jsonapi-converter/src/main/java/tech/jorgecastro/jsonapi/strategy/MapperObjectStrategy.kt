package tech.jorgecastro.jsonapi.strategy

import tech.jorgecastro.jsonapi.JsonApiMapper
import tech.jorgecastro.jsonapi.JsonApiObjectResponse
import tech.jorgecastro.jsonapi.JsonApiResponse
import kotlin.reflect.KClass

class MapperObjectStrategy : JsonApiMapperStrategy {
    override fun map(
        jsonApiResponse: JsonApiResponse,
        outputObjectRawType: KClass<*>,
        jsonApiMapper: JsonApiMapper
    ): Any? {
        val resourceId = jsonApiMapper.getJsonApiIdFrom(outputObjectRawType)

        /**
         * The attributes that have the JsonApiRelationship annotation are obtained.
         */
        val jaRelationship = jsonApiMapper.getRelationshipFromJsonApiData(outputObjectRawType)

        val input = jsonApiResponse as JsonApiObjectResponse<*>

        input.included?.let { listInclude ->
            input.data?.let { jsonApiData ->
                jsonApiMapper.mapDataPayload(
                    jsonApiData,
                    jaRelationship,
                    outputObjectRawType,
                    listInclude,
                    resourceId
                )
            }
        } ?: run {
            input.data?.let { jsonApiData -> jsonApiMapper.setDataId(jsonApiData, resourceId) }
        }

        return input.data?.attributes
    }
}
