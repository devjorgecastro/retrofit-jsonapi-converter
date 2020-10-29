package tech.jorgecastro.jsonapi

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONObject
import tech.jorgecastro.jsonapi.commons.jDeclaredFields
import tech.jorgecastro.jsonapi.commons.setListWithIgnorePrivateCase
import tech.jorgecastro.jsonapi.commons.setWithIgnorePrivateCase
import tech.jorgecastro.jsonapi.strategy.JsonApiMapperStrategy
import java.lang.IllegalArgumentException
import java.lang.reflect.ParameterizedType
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

/**
 * Json api mapper: Class for map jsonapi response to simple object(As restful)
 *
 * @constructor Create empty Json api mapper
 */
class JsonApiMapper {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /**
     * function map determines if the input to the function is an object or a list
     *
     * @param input is a JsonApi response.
     * @param outputObjectRawType is the type of the object to which you want to convert the output.
     * @return returns the desired object similar to working with Restful.
     * @throws IllegalArgumentException when the argument passed  is not of type JsonApiResponse.
     *
     * Example of usage for JsonApiObjectResponse
     * ```
     * return JsonApiMapper().map(
     *      input = jsonApiObject as JsonApiObjectResponse<*>,
     *      outputObjectRawType = classReference.kotlin
     * )
     * ```
     *
     * * Example of usage for JsonApiListResponse
     * ```
     * return JsonApiMapper().map(
     *      input = jsonApiObject as JsonApiListResponse<*>,
     *      outputObjectRawType = classReference.kotlin
     * )
     * ```
     *
     * @deprecated Please use map(JsonApiResponse, KClass<*>, JsonApiMapperStrategy) instead.
     */
    @Deprecated(message = "Use map(JsonApiResponse, KClass<*>, JsonApiMapperStrategy) instead.")
    fun map(input: JsonApiResponse, outputObjectRawType: KClass<*>): Any? {
        return if (input is JsonApiObjectResponse<*>) {
            mapToListObject(input, outputObjectRawType)
        } else {
            check(input is JsonApiListResponse<*>) {
                throw IllegalArgumentException("The argument passed is not of type JsonApiResponse")
            }
            mapToListObject(input, outputObjectRawType)
        }
    }

    /**
     * function map determines if the input to the function is an object or a list
     *
     * @param input is a JsonApi response.
     * @param outputObjectRawType is the type of the object to which you want to convert the output.
     * @param strategy is the strategy to covert to object or list
     * @return returns the desired object similar to working with Restful.
     * @throws IllegalArgumentException when the argument passed  is not of type JsonApiResponse.
     * @since 1.0.0-beta4
     *
     * Example of usage for JsonApiListResponse
     * ```
     * return JsonApiMapper().map(
     *      input = jsonApiObject as JsonApiObjectResponse<*>,
     *      outputObjectRawType = classReference.kotlin,
     *      strategy = MapperObjectStrategy()
     * )
     * ```
     *
     * * Example of usage for JsonApiListResponse
     * ```
     * return JsonApiMapper().map(
     *      input = jsonApiObject as JsonApiListResponse<*>,
     *      outputObjectRawType = classReference.kotlin,
     *      strategy = MapperListStrategy()
     * )
     * ```
     */
    fun map(
        input: JsonApiResponse,
        outputObjectRawType: KClass<*>,
        strategy: JsonApiMapperStrategy
    ): Any? {
        check((input is JsonApiListResponse<*>) || (input is JsonApiObjectResponse<*>)) {
            throw IllegalArgumentException("The argument passed is not of type JsonApiResponse")
        }
        return strategy.map(input, outputObjectRawType, this)
    }

    /**
     * mapToListObject is overloaded function that map an object or a list.
     *
     * @param input is of type [JsonApiObjectResponse] or JsonApiListResponse.
     * @param outputObjectRawType is the type of the object to which you want to convert the output.
     * @return an object or list determined by [outputObjectRawType].
     */
    private fun mapToListObject(
        input: JsonApiObjectResponse<*>,
        outputObjectRawType: KClass<*>
    ): Any? {
        val resourceId = getJsonApiIdFrom(outputObjectRawType)

        /**
         * The attributes that have the JsonApiRelationship annotation are obtained.
         */
        val jaRelationship = getRelationshipFromJsonApiData(outputObjectRawType)

        input.included?.let { listInclude ->
            input.data?.let { jsonApiData ->
                mapDataPayload(
                    jsonApiData,
                    jaRelationship,
                    outputObjectRawType,
                    listInclude,
                    resourceId
                )
            }
        } ?: run {
            input.data?.let { jsonApiData -> setDataId(jsonApiData, resourceId) }
        }

        return input.data?.attributes
    }

    /**
     * [mapToListObject] is overloaded function that map an object or a list
     *
     * @param input is of type [JsonApiListResponse] or JsonApiListResponse
     * @param outputObjectRawType is the type of the object to which you want to convert the output.
     * @return an object or list determined by [outputObjectRawType]
     */
    private fun mapToListObject(
        input: JsonApiListResponse<*>,
        outputObjectRawType: KClass<*>
    ): List<Any>? {

        val listData = input.data
        val resourceId = getJsonApiIdFrom(outputObjectRawType)

        /**
         * The attributes that have the JsonApiRelationship annotation are obtained.
         */
        val jaRelationship = getRelationshipFromJsonApiData(outputObjectRawType)

        input.included?.let { listInclude ->
            listData?.forEach loopListData@{ jsonApiData ->
                mapDataPayload(
                    jsonApiData,
                    jaRelationship,
                    outputObjectRawType,
                    listInclude,
                    resourceId
                )
            }
        } ?: run {
            listData?.forEach loopListData@{ jsonApiData -> setDataId(jsonApiData, resourceId) }
        }

        return listData?.flatMap {
            val newList = arrayListOf<Any>()
            newList.add(it.attributes!!)
            newList
        }
    }

    /**
     * Map data payload
     *
     * @param jsonApiData is the data payload in JsonApi response.
     * @param jaRelationship The attributes that have the [JsonApiRelationship] annotation are obtained.
     * @param outputObjectRawType is the type of the object to which you want to convert the output.
     * @param listInclude have list of payload included.
     * @param resourceId containt info about the id of data.
     */
    fun mapDataPayload(
        jsonApiData: JsonApiData<out Any?>,
        jaRelationship: List<JsonApiRelationshipAttribute>,
        outputObjectRawType: KClass<*>,
        listInclude: List<Any>,
        resourceId: JsonApiId
    ) {
        /**
         * All resource relationships are iterated
         */

        val relationships = (jsonApiData.relationships as? Map<*, *>)
        relationships?.keys?.forEach { key ->

            val relationship = (relationships[key] as Map<*, *>)
            if (relationship.containsKey("data")) {

                val relationshipData = relationship["data"]
                val listRelationship = if (relationshipData is ArrayList<*>) {
                    relationshipData
                } else {
                    listOf((relationshipData as Map<*, *>))
                }

                val listIncludeObjectsMaps = arrayListOf<Map<String, *>>()

                listRelationship.forEach { itemRelationship ->

                    val relationshipMap = (itemRelationship as Map<*, *>)
                    val type = relationshipMap["type"].toString()

                    if (relationshipMap.containsKey("id") && relationshipMap.containsKey("type")) {

                        /**
                         * If there are matches of the JsonApiRelationship annotation, the include data is obtained.
                         */
                        val relationshipCoincidences = jaRelationship.filter {
                            it.jsonApiRelationshipAnnotation.jsonApiResourceName == type
                        }

                        if (relationshipCoincidences.isNotEmpty()) {
                            val kClassRelationship =
                                getRawTypeRelationship(outputObjectRawType, type)

                            val includeRelationship = getObjectForIncludePayload(
                                relationshipMap["id"].toString(),
                                type,
                                listInclude,
                                kClassRelationship
                            )

                            includeRelationship?.let { includeRel ->
                                listIncludeObjectsMaps.add(mapOf(type to includeRel))
                            }
                        }
                    }

                    updateRelationshipInAttrClass(jsonApiData, listIncludeObjectsMaps, type)
                }
            }
        }

        setIdForAllDataItem(jsonApiData, resourceId)
    }

    /**
     * Get the values for a relation determined by the [type] parameter
     *
     * @param listIncludeObjectsMaps all data in the included payload.
     * @param annotation containt metadata of object type.
     * @param type is the type of return objects.
     * @return object or list with type mark by key param.
     */
    private fun getValueForRelationship(
        listIncludeObjectsMaps: ArrayList<Map<String, *>>,
        annotation: JsonApiRelationship,
        type: Any?
    ): Any? {
        val response = listIncludeObjectsMaps
            .filter { it.containsKey(annotation.jsonApiResourceName) }
            .flatMap { map ->
                val newList = arrayListOf<Any>()
                map[type]?.let {
                    newList.add(it)
                }

                newList
            }

        return if (response.count() == 1) {
            response.first()
        } else {
            response
        }
    }

    /**
     * Update the values of the relations of the expected object.
     *
     * @param jsonApiData is the data payload in JsonApi response.
     * @param listIncludeObjectsMaps all data in the included payload.
     * @param typeValue is the type of relationship class with which the value will be updated.
     */
    private fun updateRelationshipInAttrClass(
        jsonApiData: JsonApiData<out Any?>,
        listIncludeObjectsMaps: ArrayList<Map<String, *>>,
        typeValue: Any?
    ) {
        run jsonApiDataLoop@{
            val expectedObject = jsonApiData.attributes ?: return@jsonApiDataLoop

            expectedObject.javaClass.declaredFields.forEach { field ->

                // Iterate all annotations for each attribute of the class
                field.annotations.forEach { annotation ->
                    if (annotation is JsonApiRelationship &&
                        annotation.jsonApiResourceName == typeValue
                    ) {

                        // Value for the relation
                        val valueField = getValueForRelationship(
                            listIncludeObjectsMaps,
                            annotation,
                            typeValue
                        )

                        if (field.type == List::class.java &&
                            valueField !is ArrayList<*>
                        ) {
                            field.setListWithIgnorePrivateCase(expectedObject, valueField)
                        } else {
                            field.setWithIgnorePrivateCase(expectedObject, valueField)
                        }
                        return@jsonApiDataLoop
                    }
                }
            }
        }
    }

    /**
     * Sets the id of the Data Object parsed by retrofit-jsonapi-converter
     *
     * @param jsonApiData
     * @param resourceId containt info about property of class
     * selected as ID ([JsonApiId.jsonPropertyName] / [JsonApiId.propertyName])
     *
     * @see JsonApiData
     * @see JsonApiId
     */
    fun setDataId(
        jsonApiData: JsonApiData<out Any?>,
        resourceId: JsonApiId
    ) {
        jsonApiData.javaClass.declaredFields.forEach { field ->
            if (field.name == resourceId.propertyName ||
                field.name == resourceId.jsonPropertyName
            ) {
                field.isAccessible = true
                val idValue = field.get(jsonApiData)
                field.isAccessible = false
                jsonApiData.attributes?.javaClass?.declaredFields?.forEach { jsonApiDataField ->
                    if (jsonApiDataField.name == "id") {
                        jsonApiDataField.setWithIgnorePrivateCase(
                            jsonApiData.attributes,
                            "$idValue"
                        )
                    }
                }
            }
        }
    }

    /**
     * Sets the id for all Objects of class that represent attributes payload
     *
     * @param jsonApiData is the information received from the service
     * @param resourceId containt info about property of class
     * selected as ID ([JsonApiId.jsonPropertyName] / [JsonApiId.propertyName]).
     * In other words it is the id attribute of the object where I want to map the response
     *
     * @see JsonApiData
     * @see JsonApiId
     */
    private fun setIdForAllDataItem(
        jsonApiData: JsonApiData<out Any?>,
        resourceId: JsonApiId
    ) {
        jsonApiData.javaClass.declaredFields.forEach { field ->

            /**
             * It is verified by propertyName and jsonPropertyName in case the attribute
             * does not have the annotation @Json(name = "id")
             */
            if (field.name == resourceId.propertyName ||
                field.name == resourceId.jsonPropertyName
            ) {
                field.isAccessible = true
                val idValue = field.get(jsonApiData)
                field.isAccessible = false

                val jsonApiDataField = jsonApiData.attributes?.javaClass?.declaredFields
                    ?.first { it.name == "id" }

                jsonApiDataField?.run {
                    isAccessible = true
                    set(jsonApiData.attributes, "$idValue")
                    isAccessible = false
                }
            }
        }
    }

    /**
     * The attributes that have the [JsonApiRelationship] annotation are obtained.
     *
     * @param input is the expected object in data payload of JsonApi response.
     * @return returns a list of fields filtered by the [JsonApiRelationship] annotation.
     *
     * @see JsonApiRelationshipAttribute
     */
    fun getRelationshipFromJsonApiData(input: KClass<*>): List<JsonApiRelationshipAttribute> {

        val jaRelationship = arrayListOf<JsonApiRelationshipAttribute>()

        val fields = input.java.declaredFields
        fields.forEach { field ->
            val annotations = field.annotations
            annotations.forEach { annotation ->
                if (annotation is JsonApiRelationship) {

                    val jaRelationshipAttr = JsonApiRelationshipAttribute(
                        field = field,
                        jsonApiRelationshipAnnotation = annotation
                    )
                    jaRelationship.add(jaRelationshipAttr)
                }
            }
        }
        return jaRelationship
    }

    /**
     * Get the object conformed by the kClassRelationship class passed by the parameter
     *
     * @param id
     * @param type the type of relationship to look for.
     * @param includes payload with data for all relationship.
     * @param kClassRelationship is the class of the object to return.
     *
     * @return an object converted by moshi taking the parameter [kClassRelationship]
     */
    private fun getObjectForIncludePayload(
        id: String,
        type: String,
        includes: List<Any>,
        kClassRelationship: KClass<*>?
    ): Any? {

        var relationshipObject: Any? = null

        kClassRelationship?.let {

            val kClassRelationshipId = getJsonApiIdFrom(kClassRelationship)

            includes.forEach { include ->
                val includeMap = (include as Map<*, *>)
                if (includeMap["type"] == type && includeMap[kClassRelationshipId.jsonPropertyName] == id) {
                    val attributes = (includeMap["attributes"] as Map<*, *>)
                    val jsonObjetString = JSONObject(attributes).toString()
                    relationshipObject =
                        moshi.adapter(kClassRelationship.java).fromJson(jsonObjetString)

                    relationshipObject?.jDeclaredFields()?.firstOrNull { it.name == "id" }?.run {
                        val valueForId = include[kClassRelationshipId.jsonPropertyName]
                        setWithIgnorePrivateCase(relationshipObject, "$valueForId")
                    }
                }
            }
        }
        return relationshipObject
    }

    /**
     * Function to obtain the RawType of a relation through the name specified
     * in the name attribute of JsonApiRelationship
     *
     * @param mainRawType is [KClass] of expected object.
     * @param relationship is the type of relationship you want to find.
     * @return returns an [KClass] specified by the [relationship] parameter.
     */
    private fun getRawTypeRelationship(mainRawType: KClass<*>, relationship: String): KClass<*>? {

        var kClassResponse: KClass<*>? = null

        /**
         * All the relationships are obtained for each of the fields and then it is verified
         * that the relationship argument is equal to the [JsonApiRelationship.jsonApiResourceName]
         * of the JsonApiRelationship attribute.
         */
        run loop@{
            mainRawType.java.declaredFields.forEach { field ->

                // All annotations of type JsonApiRelationship are obtained
                val jsonApiRelationshipFiltered =
                    field.annotations.filterIsInstance<JsonApiRelationship>()
                jsonApiRelationshipFiltered.forEach { jsonapiRelationship ->
                    if (jsonapiRelationship.jsonApiResourceName == relationship) {

                        if (field.type == List::class.java &&
                            (field.genericType is ParameterizedType)
                        ) {

                            val classResponse =
                                (field.genericType as ParameterizedType).actualTypeArguments.firstOrNull() as Class<*>
                            kClassResponse = Reflection.createKotlinClass(classResponse)
                            return@loop
                        } else {
                            kClassResponse = field.type.kotlin
                            return@loop
                        }
                    }
                }
            }
        }

        return kClassResponse
    }

    /**
     * Get json api id from the expected object
     *
     * @param classRef is the expected object
     * @return [JsonApiId] object
     *
     * @see JsonApiId
     */
    fun getJsonApiIdFrom(classRef: KClass<*>): JsonApiId {
        var jsonPropertyName: String? = null
        var propertyName = ""
        var fieldName: String? = null
        run fieldsLoop@{
            classRef.java.declaredFields.forEach { field ->
                val annotations = field.annotations
                propertyName = field.name
                if (annotations.isNotEmpty()) {
                    val annotation = annotations.filterIsInstance<Json>().firstOrNull()
                    if (annotation != null) {
                        fieldName = annotation.name
                        if (fieldName == "id") return@fieldsLoop
                    } else {
                        fieldName = propertyName
                        if (fieldName == "id") return@fieldsLoop
                    }
                } else {
                    fieldName = propertyName
                    if (fieldName == "id") return@fieldsLoop
                }
            }
        }

        if (fieldName == "id") {
            jsonPropertyName = fieldName
        }

        return JsonApiId(jsonPropertyName, propertyName)
    }
}
