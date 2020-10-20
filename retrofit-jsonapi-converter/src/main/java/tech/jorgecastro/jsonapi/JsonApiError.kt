package tech.jorgecastro.jsonapi

/**
 * @property jsonapi provides info about the json api response
 * @property errors is the array with the errors of response
 *
 * @see <a href="https://jsonapi.org/format/#errors">Json Api error specification</a>
 */
class JsonApiError {
    var jsonapi: JsonApiErrorInfo? = null
    var errors: List<JsonApiErrorData>? = null
}

/**
 * Containt info about the json api response
 *
 * @property version is the body that provides version info of response
 * @see JsonApiError
 */
class JsonApiErrorInfo {
    var version: String? = null
}

/**
 * Containt the list of errors
 *
 * @property id a unique identifier for this particular occurrence of the problem.
 * @property status the HTTP status code applicable to this problem, expressed as a string value.
 * @property code an application-specific error code, expressed as a string value.
 * @property title a short, human-readable summary of the problem that <b>SHOULD NOT</b> change
 * from occurrence to occurrence of the problem, except for purposes of localization.
 * @property detail a human-readable explanation specific to this occurrence of the problem.
 * Like [title], this field’s value can be localized.
 * @property meta a meta object containing non-standard meta-information about the error.
 * @property source an object containing references to the source of the error [JsonApiSource]
 *
 *
 * At the moment links, source, meta attributes are not supported.
 *
 * @see JsonApiSource
 * @see <a href="https://jsonapi.org/format/#errors" target="_blank">Json Api error specification</a>
 */
@UpcomingChanges(description = "Support for links, source, meta attributes")
class JsonApiErrorData {
    var id: String = ""
    var status: Int = 0
    var code: String? = null
    var title: String? = null
    var detail: String? = null
    var meta: Map<String, *>? = null
    var source: JsonApiSource? = null
}

/**
 * An object containing references to the source of the error,
 * optionally including any of the following members: [pointer] and [parameter]
 *
 * @property pointer a JSON Pointer [RFC6901] to the associated entity in the request document
 * [e.g. "/data" for a primary data object, or "/data/attributes/title" for a specific attribute].
 * @property parameter a string indicating which URI query parameter caused the error.
 *
 * @see <a href="https://tools.ietf.org/html/rfc6901" target="_blank">RFC6901</a>
 */
class JsonApiSource {
    var pointer: String? = null
    var parameter: String? = null
}