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


class JsonApiResponse<T> {
    var data: JsonApiData<T>? = null
    var included: List<Any>? = null
}

class JsonApiListResponse<T> {
    var data: List<JsonApiData<T>>? = null
    var included: List<Any>? = null
}