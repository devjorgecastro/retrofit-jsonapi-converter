package tech.jorgecastro.jsonapi

/**
 * Class that represent info about ID property
 *
 * @param jsonPropertyName is the value inside a @Json for an id attribute.
 * e.g. @Json(name = "jsonPropertyName")
 * @param propertyName: is the class attribute annotated with @Json (name = "id")
 */
data class JsonApiId(val jsonPropertyName: String?, val propertyName: String?)