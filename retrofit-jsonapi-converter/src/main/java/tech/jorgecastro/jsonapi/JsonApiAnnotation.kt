package tech.jorgecastro.jsonapi

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.FUNCTION)
@Retention(RetentionPolicy.RUNTIME)
annotation class JsonApiMethod

@Target(AnnotationTarget.CLASS)
@Retention(RetentionPolicy.RUNTIME)
annotation class JsonApiResource(val name: String)

@Target(AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class JsonApiField(val name: String)



/**
 * A class attribute with this annotation linked to the relationship of the jsonApi response.
 *
 * @param jsonApiResourceName is the name of the JsonApiResource
 * @param jsonAttrName is the name of the attribute in the json response
 *
 * @see JsonApiResource
 */
@Target(AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class JsonApiRelationship(val jsonApiResourceName: String, val jsonAttrName: String)