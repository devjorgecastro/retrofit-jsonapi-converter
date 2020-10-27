package tech.jorgecastro.jsonapi.adapter

import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.JsonApiMethod
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonApiCallAdapterFactory private constructor() :
    CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        annotations.filterIsInstance<JsonApiMethod>().firstOrNull() ?: return null
        require(returnType is ParameterizedType) { return null }

        val parameterizedType = getParameterUpperBound(0, returnType as ParameterizedType)

        return when (val rawType = getRawType(returnType)) {
            Single::class.java, Observable::class.java -> {
                JsonApiRxJava2CallAdapter<Any>(parameterizedType, rawType)
            }
            Flow::class.java -> {
                JsonApiFlowCallAdapter<Any>(parameterizedType)
            }
            else -> {
                JsonApiCallAdapter<Any>(parameterizedType)
            }
        }
    }

    companion object {
        @JvmStatic
        fun create() = JsonApiCallAdapterFactory()
    }
}
