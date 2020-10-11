package tech.jorgecastro.jsonapi

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONObject
import tech.jorgecastro.jsonapi.commons.setListWithIgnorePrivateCase
import tech.jorgecastro.jsonapi.commons.jDeclaredFields
import tech.jorgecastro.jsonapi.commons.setWithIgnorePrivateCase
import java.lang.IllegalArgumentException
import java.lang.reflect.ParameterizedType
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

class JsonApiMapper {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun map(input: JsonApiResponse, rawType: KClass<*>): Any? {
        return if (input is JsonApiObjectResponse<*>) {
            mapToListObject(input, rawType)
        } else {
            check (input is JsonApiListResponse<*>) {
                throw IllegalArgumentException("The argument passed is not of type JsonApiResponse")
            }
            mapToListObject(input, rawType)
        }
    }
    
    
    private fun mapToListObject(input: JsonApiObjectResponse<*>, rawType: KClass<*>): Any? {
        val resourceId = getJsonApiIdFrom(rawType)

        /**
         * The attributes that have the JsonApiRelationship annotation are obtained.
         */
        val jaRelationship = getRelationshipFromJsonApiData(rawType)

        input.included?.let { listInclude ->
            input.data?.let {  jsonApiData ->
                mapDataPayload(jsonApiData, jaRelationship, rawType, listInclude, resourceId)
            }
        } ?: run {
            input.data?.let { jsonApiData -> setDataId(jsonApiData, resourceId) }
        }

        return input.data?.attributes
    }


    private fun mapToListObject(input: JsonApiListResponse<*>, rawType: KClass<*>): List<Any>? {
        
        val listData = input.data
        val resourceId = getJsonApiIdFrom(rawType)

        /**
         * The attributes that have the JsonApiRelationship annotation are obtained.
         */
        val jaRelationship = getRelationshipFromJsonApiData(rawType)

        input.included?.let { listInclude ->
            listData?.forEach loopListData@ {  jsonApiData ->
                mapDataPayload(jsonApiData, jaRelationship, rawType, listInclude, resourceId)
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

    private fun mapDataPayload(
        jsonApiData: JsonApiData<out Any?>,
        jaRelationship: List<JsonApiRelationshipAttribute>,
        rawType: KClass<*>,
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
                }
                else {
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
                            val kClassRelationship = getRawTypeRelationship(rawType, type)

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

    private fun getValueForRelationship(
        listIncludeObjectsMaps: ArrayList<Map<String, *>>,
        annotation: JsonApiRelationship,
        key: Any?
    ): Any? {
        val response = listIncludeObjectsMaps
            .filter { it.containsKey(annotation.jsonApiResourceName) }
            .flatMap { map ->
                val newList = arrayListOf<Any>()
                map[key]?.let {
                    newList.add(it)
                }

                newList
            }


        return if (response.count() == 1) {
            response.first()
        }
        else {
            response
        }
    }

    private fun updateRelationshipInAttrClass(
        jsonApiData: JsonApiData<out Any?>,
        listIncludeObjectsMaps: ArrayList<Map<String, *>>,
        typeValue: Any?
    ) {
        run jsonApiDataLoop@{
            val dataAttributes = jsonApiData.attributes ?: return@jsonApiDataLoop

            dataAttributes.javaClass.declaredFields.forEach { field ->


                field.annotations.forEach { annotation ->
                    if (annotation is JsonApiRelationship
                        && annotation.jsonApiResourceName == typeValue) {


                        val valueField = getValueForRelationship(
                            listIncludeObjectsMaps,
                            annotation,
                            typeValue
                        )


                        if (field.type == List::class.java
                            && valueField !is ArrayList<*>) {
                            field.setListWithIgnorePrivateCase(dataAttributes, valueField)
                        }
                        else {
                            field.setWithIgnorePrivateCase(dataAttributes, valueField)
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
    private fun setDataId(
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
                        jsonApiDataField.setWithIgnorePrivateCase(jsonApiData.attributes, "$idValue")
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
                field.name == resourceId.jsonPropertyName) {
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

    private fun getRelationshipFromJsonApiData(input: KClass<*>): List<JsonApiRelationshipAttribute> {

        val jaRelationship = arrayListOf<JsonApiRelationshipAttribute>()

        val fields = input.java.declaredFields
        fields.forEach { field ->
            val annotations = field.annotations
            annotations.forEach { annotation ->
                if (annotation is JsonApiRelationship) {
                    jaRelationship.add(
                        JsonApiRelationshipAttribute(field = field, jsonApiRelationshipAnnotation = annotation)
                    )
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
                val includeMap = (include as Map<*,*>)
                if (includeMap["type"] == type && includeMap[kClassRelationshipId.jsonPropertyName] == id) {
                    val attributes = (includeMap["attributes"] as Map<*,*>)
                    val jsonObjetString = JSONObject(attributes).toString()
                    relationshipObject = moshi.adapter(kClassRelationship.java).fromJson(jsonObjetString)


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
     * Función para obtener el RawType de una relación a través del nombre especificado
     * en el atributo name de JsonApiRelationship
     */
    fun getRawTypeRelationship(mainRawType: KClass<*>, relationship: String): KClass<*>? {

        var kClassResponse: KClass<*>? = null

        /**
         * Obtenemos todas las relaciones por cada uno de los campos y luego
         * verificamos que el argumento relationship sea igual a los nombres del
         * atributo name de JsonApiRelationship
         */
        run loop@{
            mainRawType.java.declaredFields.forEach { field ->

                /**
                 * Se obtienen todas las anotaciones del tipo JsonApiRelationship
                 */
                val jsonApiRelationshipFiltered = field.annotations.filterIsInstance<JsonApiRelationship>()
                jsonApiRelationshipFiltered.forEach { jsonapiRelationship ->
                    if (jsonapiRelationship.jsonApiResourceName == relationship) {

                        if (field.type == List::class.java &&
                            (field.genericType is ParameterizedType)) {

                            val classResponse = (field.genericType as ParameterizedType).actualTypeArguments.firstOrNull() as Class<*>
                            kClassResponse = Reflection.createKotlinClass(classResponse)
                            return@loop
                        }
                        else {
                            kClassResponse = field.type.kotlin
                            return@loop
                        }
                    }
                }
            }
        }

        return kClassResponse
    }

    private fun getJsonApiIdFrom(classRef: KClass<*>): JsonApiId {
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
                    }
                    else {
                        fieldName = propertyName
                        if (fieldName == "id") return@fieldsLoop
                    }
                }
                else {
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
