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

@Target(AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class JsonApiRelationship(val name: String, val relationship: String)