package tech.jorgecastro.jsonapi.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class JsonApiCallAdapter<T>(
    private val responseType: Type
): CallAdapter<T, JsonApiRetrofitCall<T>> {
    override fun adapt(call: Call<T>): JsonApiRetrofitCall<T> {
        return JsonApiRetrofitCall(call)
    }

    override fun responseType() = responseType
}