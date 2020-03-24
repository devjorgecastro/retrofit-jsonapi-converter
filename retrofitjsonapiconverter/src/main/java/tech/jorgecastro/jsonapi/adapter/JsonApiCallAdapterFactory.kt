package tech.jorgecastro.jsonapi.adapter

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonApiCallAdapterFactory private constructor():
    CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val type = getParameterUpperBound(0, returnType as ParameterizedType)
        return JsonApiCallAdapter<Any>(type)
    }

    companion object {
        @JvmStatic
        fun create() = JsonApiCallAdapterFactory()
    }
}