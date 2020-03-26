package tech.jorgecastro.jsonapi.exception

import tech.jorgecastro.jsonapi.JsonApiError

/**
 * Thrown to indicate a problem with the JSON API
 */
class JsonException(message: String, cause: Throwable): Exception(message, cause)

/**
 * Exception to indicate when the json api structure is not compatible with the class to be converted
 */
class JsonUnsupportedClass(message: String, cause: Throwable): Exception(message, cause)


class JsonApiResponseException(
    message: String,
    val data: JsonApiError) : Exception(message, null)