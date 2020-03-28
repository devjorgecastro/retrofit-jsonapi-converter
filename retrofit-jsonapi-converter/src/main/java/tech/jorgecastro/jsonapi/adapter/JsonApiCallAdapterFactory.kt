package tech.jorgecastro.jsonapi.adapter

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.CallAdapter
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.JsonApiMethod
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonApiCallAdapterFactory private constructor():
    CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        annotations.filterIsInstance<JsonApiMethod>().firstOrNull() ?: return null
        return when (val rawType =  getRawType(returnType)) {
            Single::class.java, Observable::class.java -> {
                val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
                JsonApiRxJava2CallAdapter<Any>(responseType, rawType)
            }
            else -> {
                val type = getParameterUpperBound(0, returnType as ParameterizedType)
                return JsonApiCallAdapter<Any>(type)
            }
        }
    }

    companion object {
        @JvmStatic
        fun create() = JsonApiCallAdapterFactory()
    }
}