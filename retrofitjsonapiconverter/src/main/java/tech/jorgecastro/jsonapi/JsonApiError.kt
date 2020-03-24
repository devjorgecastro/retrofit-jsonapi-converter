package tech.jorgecastro.jsonapi


class JsonApiError {
    var jsonapi: JsonApiErrorInfo? = null
    var errors: List<JsonApiErrorData>? = null
}

class JsonApiErrorInfo {
    var version: String? = null
}

class JsonApiErrorData {
    var id: String = ""
    var status: Int = 0
    var code: String? = null
    var title: String? = null
    var detail: String? = null
}