package tech.jorgecastro.jsonapi.adapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import tech.jorgecastro.jsonapi.JsonApiError
import tech.jorgecastro.jsonapi.exception.JsonApiResponseException
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class JsonApiFlowCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Flow<T>> {
    override fun adapt(call: Call<T>): Flow<T> {
        return flow {
            emit(
                suspendCancellableCoroutine<T> { continuation ->
                    call.enqueue(object : Callback<T> {
                        override fun onFailure(call: Call<T>, t: Throwable) {
                            continuation.resumeWithException(t)
                        }

                        override fun onResponse(call: Call<T>, response: Response<T>) {

                            when (response.code()) {
                                in 200..299 -> {
                                    try {
                                        continuation.resume(response.body()!!)
                                    } catch (e: Exception) {
                                        continuation.resumeWithException(e)
                                    }
                                }
                                in 400..499 -> {
                                    val moshi =
                                        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                    val jsonObject = JSONObject(response.errorBody()?.string())
                                    val jsonApiError = moshi.adapter(JsonApiError::class.java)
                                        .fromJson(jsonObject.toString())
                                    jsonApiError?.let {
                                        val jsonApiResponseException = JsonApiResponseException(
                                            message = response.message(),
                                            data = jsonApiError
                                        )
                                        continuation.resumeWithException(jsonApiResponseException)
                                    }
                                }
                                else -> {
                                    continuation.resumeWithException(Exception())
                                }
                            }
                        }
                    })
                    continuation.invokeOnCancellation { call.cancel() }
                }
            )
        }
    }

    override fun responseType() = responseType
}
