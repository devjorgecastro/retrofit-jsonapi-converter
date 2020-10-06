package tech.jorgecastro.jsonapi

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONObject
import tech.jorgecastro.jsonapi.commons.jDeclaredFields
import tech.jorgecastro.jsonapi.commons.setWithIgnorePrivateCase
import java.lang.reflect.ParameterizedType
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

class JsonApiMapper {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun jsonApiMapToListObject(input: JsonApiResponse<*>, rawType: KClass<*>): Any? {
        val resourceId = getJsonApiIdFrom(rawType)

        /**
         * The attributes that have the JsonApiRelationship annotation are obtained.
         */
        val jaRelationship = getRelationshipFromJsonApiData(rawType)

        input.included?.let { listInclude ->
            input.data?.let {  jsonApiData ->

                /**
                 * All resource relationships are iterated
                 */

                val relationships = (jsonApiData.relationships as? Map<*,*>)
                relationships?.keys?.forEach { key ->
                    val relationship = (relationships[key] as Map<*,*>)
                    if ( relationship.containsKey("data") ) {

                        val listRelationship = (relationship["data"]  as List<Map<*,*>>)
                        val listIncludeObjectsMaps = arrayListOf<Map<String, *>>()

                        listRelationship.forEach { itemRelationship ->

                            val relationshipMap = (itemRelationship as Map<*,*>)
                            if (relationshipMap.containsKey("id") && relationshipMap.containsKey("type")) {

                                val type = relationshipMap["type"].toString()

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
                                        listIncludeObjectsMaps.add(mapOf(key.toString() to includeRel))
                                    }
                                }
                            }
                        }

                        //includeRelationship
                        run jsonApiDataLoop@ {
                            jsonApiData.attributes?.let { dataAttributes ->
                                dataAttributes.javaClass.declaredFields.forEach { field ->
                                    val annotations = field.annotations
                                    if (annotations.isNotEmpty()) {
                                        annotations.forEach { annotation ->
                                            if (annotation is JsonApiRelationship) {
                                                val valueField = listIncludeObjectsMaps
                                                    .filter { it.containsKey(annotation.jsonAttrName)}
                                                    .flatMap { map ->
                                                        val newList = arrayListOf<Any>()
                                                        map[key]?.let {
                                                            newList.add(it)
                                                        }
                                                        newList
                                                    }

                                                field.setWithIgnorePrivateCase(dataAttributes, valueField)
                                                return@jsonApiDataLoop
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                setIdForAllDataItem(jsonApiData, resourceId)
            }
        } ?: run {
            input.data?.let { jsonApiData -> setDataId(jsonApiData, resourceId) }
        }

        return input.data?.attributes
    }


    fun jsonApiMapToListObject(input: JsonApiListResponse<*>, rawType: KClass<*>): List<Any>? {
        val listData = input.data
        val resourceId = getJsonApiIdFrom(rawType)

        /**
         * The attributes that have the JsonApiRelationship annotation are obtained.
         */
        val jaRelationship = getRelationshipFromJsonApiData(rawType)

        input.included?.let { listInclude ->
            listData?.forEach loopListData@ {  jsonApiData ->

                /**
                 * All resource relationships are iterated
                 */

                val relationships = (jsonApiData.relationships as? Map<*,*>)
                relationships?.keys?.forEach { key ->
                    val relationship = (relationships[key] as Map<*,*>)
                    if ( relationship.containsKey("data") ) {


                        var listRelationship = arrayListOf<Map<*, *>>()
                        if (relationship["data"] is ArrayList<*>) {
                            listRelationship = (relationship["data"] as ArrayList<Map<*, *>>)
                        }
                        else {
                            listRelationship.add((relationship["data"] as Map<*, *>))
                        }


                        val listIncludeObjectsMaps = arrayListOf<Map<String, *>>()

                        listRelationship.forEach { itemRelationship ->

                            val relationshipMap = (itemRelationship as Map<*,*>)
                            if (relationshipMap.containsKey("id") && relationshipMap.containsKey("type")) {

                                val type = relationshipMap["type"].toString()

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
                                        listIncludeObjectsMaps.add(mapOf(key.toString() to includeRel))
                                    }
                                }
                            }
                        }

                        //includeRelationship
                        run jsonApiDataLoop@ {
                            jsonApiData.attributes?.let { dataAttributes ->
                                dataAttributes.javaClass.declaredFields.forEach { field ->
                                    val annotations = field.annotations
                                    if (annotations.isNotEmpty()) {
                                        annotations.forEach { annotation ->
                                            if (annotation is JsonApiRelationship) {
                                                val valueField = listIncludeObjectsMaps
                                                    .filter { it.containsKey(annotation.jsonAttrName)}
                                                    .flatMap { map ->
                                                        val newList = arrayListOf<Any>()
                                                        map[key]?.let {
                                                            newList.add(it)
                                                        }
                                                        newList
                                                    }

                                                field.setWithIgnorePrivateCase(dataAttributes, valueField)
                                                return@jsonApiDataLoop
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                setIdForAllDataItem(jsonApiData, resourceId)
            }
        } ?: run {
            listData?.forEach loopListData@{ jsonApiData -> setDataId(jsonApiData, resourceId) }
        }

        return input.data?.flatMap {
            val newList = arrayListOf<Any>()
            newList.add(it.attributes!!)
            newList
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