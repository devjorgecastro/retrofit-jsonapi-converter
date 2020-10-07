package tech.jorgecastro.jsonapi

import java.lang.reflect.Field

/**
 * Class containing information about the class attribute annotated with [JsonApiRelationship]
 *
 * @param field represent a class attribute annotated with [JsonApiRelationship]
 * @param jsonApiRelationshipAnnotation is the [JsonApiRelationship] annotation
 * with specified information about your relationship
 *
 * @see JsonApiRelationship
 */
data class JsonApiRelationshipAttribute(
    val field: Field,
    val jsonApiRelationshipAnnotation: JsonApiRelationship
)
