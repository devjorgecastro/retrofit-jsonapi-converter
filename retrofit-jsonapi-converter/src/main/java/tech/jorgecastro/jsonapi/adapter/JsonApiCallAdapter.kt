package tech.jorgecastro.jsonapi.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class JsonApiCallAdapter<T>(
    private val responseType: Type
): CallAdapter<T, JsonApiRetrofitCallAdapter<T>> {
    override fun adapt(call: Call<T>): JsonApiRetrofitCallAdapter<T> {
        return JsonApiRetrofitCallAdapter(call)
    }

    override fun responseType() = responseType
}
