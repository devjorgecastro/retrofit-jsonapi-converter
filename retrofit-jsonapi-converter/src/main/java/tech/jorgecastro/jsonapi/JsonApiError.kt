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
 *
 *
 * At the moment links, source, meta attributes are not supported.
 *
 * @see <a href="https://jsonapi.org/format/#errors">Json Api error specification</a>
 */
@UpcomingChanges(description = "Support for links, source, meta attributes")
class JsonApiErrorData {
    var id: String = ""
    var status: Int = 0
    var code: String? = null
    var title: String? = null
    var detail: String? = null
}