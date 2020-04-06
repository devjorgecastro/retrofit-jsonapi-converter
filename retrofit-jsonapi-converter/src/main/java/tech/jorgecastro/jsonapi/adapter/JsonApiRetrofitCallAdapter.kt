package tech.jorgecastro.jsonapi.adapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Request
import okio.Timeout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tech.jorgecastro.jsonapi.JsonApiError
import tech.jorgecastro.jsonapi.exception.JsonApiResponseException

class JsonApiRetrofitCallAdapter<T>(val call: Call<T>): Call<T> {
    @Throws(Exception::class)
    override fun enqueue(callback: Callback<T>) {
        call.enqueue(object: Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onFailure(call, t)
            }

            @Throws(Exception::class)
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val httpCode = response.code()

                if (httpCode in 400..499) {
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    val jsonApiError = moshi.adapter(JsonApiError::class.java).fromJson(jsonObject.toString())
                    jsonApiError?.let {
                        callback.onFailure(call, JsonApiResponseException(message = response.message(), data = jsonApiError))
                    }
                }
                else {
                    callback.onResponse(call, response)
                }
            }

        })
    }

    override fun isExecuted() = call.isExecuted

    override fun clone(): Call<T> {
        return call.clone()
    }

    override fun isCanceled() = call.isCanceled

    override fun cancel() {
        call.cancel()
    }

    override fun execute(): Response<T> {
        return call.execute()
    }

    override fun request(): Request {
        return call.request()
    }

    override fun timeout(): Timeout {
        return call.timeout()
    }
}