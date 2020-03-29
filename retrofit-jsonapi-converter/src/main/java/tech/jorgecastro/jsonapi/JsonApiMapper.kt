package tech.jorgecastro.jsonapi

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONObject
import tech.jorgecastro.jsonapi.dto.Article
import tech.jorgecastro.jsonapi.dto.People
import tech.jorgecastro.jsonapi.dto.ZoneCoverage
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

class JsonApiMapper {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    data class JAID(val jsonPropertyName: String?, val propertyName: String?)
    data class JARelationship(val field: Field, val jsonApiRelationshipAnnotation: JsonApiRelationship)


    fun jsonApiMapToListObject(input: JsonApiResponse<*>, rawType: KClass<*>): Any? {
        val resourceId = JsonApiID(rawType)

        /**
         * Se verifican los atributos que tengan la anotación JsonApiRelationship
         */
        val jaRelationship = getRelationshipFromJsonApiData(rawType)

        input.included?.let { listInclude ->
            input.data?.let {  jsonApiData ->

                /**
                 * Se recorren todas las relaciones del recurso
                 */


                val relationships = (jsonApiData.relationships as? Map<*,*>)
                relationships?.keys?.forEach { key ->
                    val relationship = (relationships[key] as Map<*,*>)
                    if ( relationship.containsKey("data") ) {

                        val listRelationship = (relationship["data"]  as List< Map<*,*>>)
                        val listIncludeObjectsMaps = arrayListOf<Map<String, *>>()

                        listRelationship.forEach { relationship ->

                            val relationshipMap = (relationship as Map<*,*>)
                            if (relationshipMap.containsKey("id") && relationshipMap.containsKey("type")) {

                                val type = relationshipMap["type"].toString()

                                /**
                                 * Si existen coincidencias de la anotación JsonApiRelationship se obtienen los datos de include
                                 */
                                val relationshipCoincidences = jaRelationship.filter { it.jsonApiRelationshipAnnotation.name == type }
                                if (relationshipCoincidences.isNotEmpty()) {
                                    //val kClassRelationship = getRelationshipReference(type)
                                    val kClassRelationship = getRawTypeRelationship(rawType, type)
                                    val includeRelationship = getIncludeObject(relationshipMap["id"].toString(), type, listInclude, kClassRelationship)
                                    includeRelationship?.let { includeRel ->
                                        listIncludeObjectsMaps.add(mapOf(key.toString() to includeRel))
                                    }
                                }
                            }
                        }

                        //includeRelationship
                        run jsonApiDataLoop@ {
                            jsonApiData.attributes?.let { dataAttributes ->
                                dataAttributes?.javaClass?.declaredFields?.forEach { field ->
                                    val annotations = field.annotations
                                    if (annotations.isNotEmpty()) {
                                        annotations.forEach { annotation ->
                                            if (annotation is JsonApiRelationship) {
                                                val valueField = listIncludeObjectsMaps
                                                    .filter { it.containsKey(annotation.relationship)}
                                                    .flatMap { map ->
                                                        val newList = arrayListOf<Any>()
                                                        map[key]?.let {
                                                            newList.add(it)
                                                        }
                                                        newList
                                                    }

                                                field.isAccessible = true
                                                field.set(dataAttributes, valueField)
                                                return@jsonApiDataLoop
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //id
                jsonApiData.javaClass.declaredFields?.forEach { field ->
                    if (field.name == resourceId.propertyName) {
                        field.isAccessible = true
                        val idValue = field.get(jsonApiData)
                        jsonApiData.attributes?.javaClass?.declaredFields?.forEach {field ->
                            if (field.name == "id") {
                                field.isAccessible = true
                                field.set(jsonApiData.attributes, "$idValue")
                            }
                        }
                    }
                }


            }
        } ?: run {
            input.data?.let { jsonApiData ->
                //id
                jsonApiData.javaClass.declaredFields?.forEach { field ->
                    if (field.name == resourceId.propertyName ||
                        field.name == resourceId.jsonPropertyName) {
                        field.isAccessible = true
                        val idValue = field.get(jsonApiData)
                        jsonApiData.attributes?.javaClass?.declaredFields?.forEach {field ->
                            if (field.name == "id") {
                                field.isAccessible = true
                                field.set(jsonApiData.attributes, "$idValue")
                            }
                        }
                    }
                }
            }
        }

        return input.data?.attributes
    }

    fun jsonApiMapToListObject(input: JsonApiListResponse<*>, rawType: KClass<*>): List<Any>? {
        val listData = input.data
        //val kClassReferenceGenericType = T::class
        //val resourceId = JsonApiID(T::class)
        val resourceId = JsonApiID(rawType)

        /**
         * Se verifican los atributos que tengan la anotación JsonApiRelationship
         */
        //val jaRelationship = getRelationshipFromJsonApiData(T::class)
        val jaRelationship = getRelationshipFromJsonApiData(rawType)

        input.included?.let { listInclude ->
            listData?.forEach loopListData@ {  jsonApiData ->

                /**
                 * Se recorren todas las relaciones del recurso
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

                        listRelationship.forEach { relationship ->

                            val relationshipMap = (relationship as Map<*,*>)
                            if (relationshipMap.containsKey("id") && relationshipMap.containsKey("type")) {

                                val type = relationshipMap["type"].toString()

                                /**
                                 * Si existen coincidencias de la anotación JsonApiRelationship se obtienen los datos de include
                                 */
                                val relationshipCoincidences = jaRelationship.filter { it.jsonApiRelationshipAnnotation.name == type }
                                if (relationshipCoincidences.isNotEmpty()) {
                                    val kClassRelationship = getRawTypeRelationship(rawType, type)
                                    //val kClassRelationship = getRelationshipReference(type)
                                    val includeRelationship = getIncludeObject(relationshipMap["id"].toString(), type, listInclude, kClassRelationship)
                                    includeRelationship?.let { includeRel ->
                                        listIncludeObjectsMaps.add(mapOf(key.toString() to includeRel))
                                    }
                                }
                            }
                        }

                        //includeRelationship
                        run jsonApiDataLoop@ {
                            jsonApiData.attributes?.let { dataAttributes ->
                                dataAttributes?.javaClass?.declaredFields?.forEach { field ->
                                    val annotations = field.annotations
                                    if (annotations.isNotEmpty()) {
                                        annotations.forEach { annotation ->
                                            if (annotation is JsonApiRelationship) {
                                                val valueField = listIncludeObjectsMaps
                                                    .filter { it.containsKey(annotation.relationship)}
                                                    .flatMap { map ->
                                                        val newList = arrayListOf<Any>()
                                                        map[key]?.let {
                                                            newList.add(it)
                                                        }
                                                        newList
                                                    }

                                                field.isAccessible = true
                                                field.set(dataAttributes, valueField)
                                                return@jsonApiDataLoop
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //id
                jsonApiData.javaClass.declaredFields?.forEach { field ->
                    if (field.name == resourceId.propertyName ||
                        field.name == resourceId.jsonPropertyName) {
                        field.isAccessible = true
                        val idValue = field.get(jsonApiData)
                        jsonApiData.attributes?.javaClass?.declaredFields?.forEach {field ->
                            if (field.name == "id") {
                                field.isAccessible = true
                                field.set(jsonApiData.attributes, "${idValue}")
                            }
                        }
                    }
                }


            }
        } ?: run {
            listData?.forEach loopListData@{ jsonApiData ->
                //id
                jsonApiData.javaClass.declaredFields?.forEach { field ->
                    if (field.name == resourceId.propertyName ||
                        field.name == resourceId.jsonPropertyName) {
                        field.isAccessible = true
                        val idValue = field.get(jsonApiData)
                        jsonApiData.attributes?.javaClass?.declaredFields?.forEach {field ->
                            if (field.name == "id") {
                                field.isAccessible = true
                                field.set(jsonApiData.attributes, "${idValue}")
                            }
                        }
                    }
                }
            }
        }

        return input.data?.flatMap {
            val newList = arrayListOf<Any>()
            newList.add(it.attributes!!)
            newList
        }
    }

    inline fun getRelationshipFromJsonApiData(input: KClass<*>): List<JARelationship> {

        var jaRelationship = arrayListOf<JARelationship>()

        val fields = input.java.declaredFields
        fields.forEach { field ->
            val annotations = field.annotations
            annotations.forEach { annotation ->
                if (annotation is JsonApiRelationship) {
                    jaRelationship.add(
                        JARelationship(field = field, jsonApiRelationshipAnnotation = annotation)
                    )
                }
            }
        }

        return jaRelationship
    }

    fun  getIncludeObject(id: String, type: String, includes: List<Any>, kClassRelationship: KClass<*>?): Any? {

        var relationshipObject: Any? = null

        kClassRelationship?.let {
            val kClassRelationshipId = JsonApiID(kClassRelationship)
            includes.forEach { include ->
                val includeMap = (include as Map<*,*>)
                if (includeMap["type"] == type && includeMap[kClassRelationshipId.jsonPropertyName] == id) {
                    val attributes = (includeMap["attributes"] as Map<*,*>)
                    val jsonObjetString = JSONObject(attributes).toString()
                    relationshipObject = moshi.adapter(kClassRelationship.java).fromJson(jsonObjetString)
                    val idField = relationshipObject?.javaClass?.declaredFields?.filter { it.name == "id" }?.firstOrNull()
                    idField?.isAccessible = true
                    val valueForId = include[kClassRelationshipId.jsonPropertyName]
                    idField?.set(relationshipObject, "$valueForId")
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
                    if (jsonapiRelationship.name == relationship) {

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

    @Deprecated(message = "This function is deprecated")
    inline fun getRelationshipReference(relationship: String): KClass<*>? {
        var response: KClass<*>? = null

        run annotationLoop@ {
            annotationList.forEach { kclass ->
                kclass.annotations.forEach { annotation ->

                    (annotation as? JsonApiResource)?.let {
                        if (annotation.name == relationship) {
                            response = kclass
                            return@annotationLoop
                        }
                    }
                }
            }
        }
        return response
    }

    fun JsonApiID(kclass: KClass<*>): JAID {
        var jsonPropertyName: String? = null
        var propertyName = ""
        var fieldName: String? = null
        run fieldsLoop@{
            kclass.java.declaredFields.forEach { field ->
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

        return JAID(jsonPropertyName, propertyName)
    }
}


val annotationList = listOf(
    ZoneCoverage::class,
    Article::class,
    People::class
)