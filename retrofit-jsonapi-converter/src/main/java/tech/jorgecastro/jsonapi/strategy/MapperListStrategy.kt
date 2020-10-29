package tech.jorgecastro.jsonapi.strategy

import tech.jorgecastro.jsonapi.JsonApiListResponse
import tech.jorgecastro.jsonapi.JsonApiMapper
import tech.jorgecastro.jsonapi.JsonApiResponse
import kotlin.reflect.KClass

class MapperListStrategy : JsonApiMapperStrategy {
    override fun map(
        jsonApiResponse: JsonApiResponse,
        outputObjectRawType: KClass<*>,
        jsonApiMapper: JsonApiMapper
    ): Any? {
        val input = jsonApiResponse as JsonApiListResponse<*>
        val listData = input.data
        val resourceId = jsonApiMapper.getJsonApiIdFrom(outputObjectRawType)

        /**
         * The attributes that have the JsonApiRelationship annotation are obtained.
         */
        val jaRelationship = jsonApiMapper.getRelationshipFromJsonApiData(outputObjectRawType)

        input.included?.let { listInclude ->
            listData?.forEach loopListData@{ jsonApiData ->
                jsonApiMapper.mapDataPayload(
                    jsonApiData,
                    jaRelationship,
                    outputObjectRawType,
                    listInclude,
                    resourceId
                )
            }
        } ?: run {
            listData?.forEach loopListData@{ jsonApiData ->
                jsonApiMapper.setDataId(
                    jsonApiData,
                    resourceId
                )
            }
        }

        return listData?.flatMap {
            val newList = arrayListOf<Any>()
            newList.add(it.attributes!!)
            newList
        }
    }
}
