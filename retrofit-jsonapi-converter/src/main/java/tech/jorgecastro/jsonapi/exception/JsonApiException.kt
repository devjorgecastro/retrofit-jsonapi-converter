package tech.jorgecastro.jsonapi.exception

import com.squareup.moshi.JsonDataException
import tech.jorgecastro.jsonapi.JsonApiError

/**
 * Class to represent a problem with parsing of JSON API
 *
 * [JsonException] extend from [RuntimeException]
 *
 * @param message display a description of the error type
 * @param cause provides detailed information on the source of error
 *
 * @see RuntimeException
 * @see <a href="https://wiki.c2.com/?RuntimeException">RuntimeException explanation</a>
 */
class JsonException(message: String, cause: Throwable): RuntimeException(message, cause)


/**
 * Exception to indicate when the json api structure is not compatible with the class to be converted
 *
 * @see JsonDataException
 */
class JsonUnsupportedClass(message: String, cause: Throwable): Exception(message, cause)


/**
 * This exception is thrown when the JsonApi response is a set of errors handled by your application.
 *
 * @param message display a description of the error type
 * @param data is [JsonApiError] that containt the body of jsonApi response for error case
 */
class JsonApiResponseException(
    message: String,
    val data: JsonApiError) : Throwable(message, null)
