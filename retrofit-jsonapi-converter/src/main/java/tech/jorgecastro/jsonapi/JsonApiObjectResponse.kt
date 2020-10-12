package tech.jorgecastro.jsonapi

class Link {
    var related: String = ""
    var self: String = ""
}

/**
 * @property attributes is the class to which you want to map the information. e.g. Articles
 */
class JsonApiData<T> {
    var id: String = ""
    var type: String = ""
    var attributes: T? = null
    var links: Link? = null
    var relationships: Map<String, *>? = null
}


open class JsonApiResponse(var included: List<Any>? = null)

class JsonApiObjectResponse<T> : JsonApiResponse() {
    var data: JsonApiData<T>? = null
}

class JsonApiListResponse<T> : JsonApiResponse() {
    var data: List<JsonApiData<T>>? = null
}
