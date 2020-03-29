package tech.jorgecastro.jsonapi

import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.full.memberExtensionFunctions


fun JsonApiListResponse<*>.getList(rawType: KClass<*>) {
    val relationships = DataRelationship.getDataRelationship(rawType)
    val b = this::class.memberExtensionFunctions
    val a = relationships
}

class JsonApiMapperNew {

    /**
     * JsonApiMapper solo soporta un solo tipo de Data
     */
    inline fun <reified T: Any>getList() {

        val relationships = DataRelationship.getDataRelationship(T::class)
        val a = relationships
    }
}

/**
 * JARelationship es una clase para almacenar el estado de un campo con su anotación del tipo JsonApiRelationship
 */
data class JARelationship(val field: Field, val jsonApiRelationshipAnnotation: JsonApiRelationship)

class DataRelationship {

    companion object {

        /**
         * Obtiene todas las Relaciones de un objeto data
         */
        inline fun getDataRelationship(input: KClass<*>): List<JARelationship> {

            var jaRelationship = listOf<JARelationship>()
            /**
             * Get all fields of class
             */
            val fields = input.java.declaredFields

            fields.forEach { field ->
                val filteredFields = field.annotations.filter { it is JsonApiRelationship }
                jaRelationship =
                    filteredFields
                        .map {
                            JARelationship(field = field, jsonApiRelationshipAnnotation = it as JsonApiRelationship)
                        }
            }
            return jaRelationship
        }
    }
}