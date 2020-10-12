package tech.jorgecastro.jsonapi

/**
 * Annotation for coming changes
 */
@Target(AnnotationTarget.CLASS)
annotation class UpcomingChanges(val description: String)

@Target(AnnotationTarget.FUNCTION)
@Retention
annotation class JsonApiMethod

@Target(AnnotationTarget.CLASS)
@Retention
annotation class JsonApiResource(val type: String)

@Target(AnnotationTarget.FIELD)
@Retention
annotation class JsonApiField(val name: String)

/**
 * A class attribute with this annotation linked to the relationship of the jsonApi response.
 *
 * @param jsonApiResourceName is the name of the [JsonApiResource]
 * @param jsonAttrName is the name of the attribute in the json response
 *
 * @see JsonApiResource
 */
@Target(AnnotationTarget.FIELD)
@Retention
annotation class JsonApiRelationship(val jsonApiResourceName: String, val jsonAttrName: String)
