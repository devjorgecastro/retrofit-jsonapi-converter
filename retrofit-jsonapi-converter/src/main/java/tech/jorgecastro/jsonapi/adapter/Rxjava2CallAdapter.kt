package tech.jorgecastro.jsonapi.adapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONObject
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import tech.jorgecastro.jsonapi.JsonApiError
import tech.jorgecastro.jsonapi.exception.JsonApiResponseException
import java.lang.reflect.Type

class JsonApiRxJava2CallAdapter<R>(
    private val responseType: Type,
    private val rawType: Class<*>
) : CallAdapter<R, Any> {
    override fun adapt(call: Call<R>): Any {
        return when(rawType) {
            Observable::class.java -> getObservable(call)
            else -> getSingleObservable(call)
        }
    }

    override fun responseType() =  responseType

    private fun getObservable(call: Call<R>): Observable<*> {
        return Observable.create<Any> { emitter ->
            call.enqueue(object: Callback<R> {
                override fun onFailure(call: Call<R>, t: Throwable) {
                    emitter.onError(t)
                }

                override fun onResponse(call: Call<R>, response: Response<R>) {

                    val httpCode = response.code()

                    if (httpCode in 200..299) {
                        emitter.onNext(response.body()!!)
                        emitter.onComplete()
                    }
                    else if (httpCode in 400..499) {
                        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val jsonApiError = moshi.adapter(JsonApiError::class.java).fromJson(jsonObject.toString())
                        jsonApiError?.let {
                            emitter.onError(
                                JsonApiResponseException(message = response.message(), data = jsonApiError)
                            )
                        }
                    }
                    else {
                        emitter.onError(Exception())
                    }
                }
            })
        }
    }

    private fun getSingleObservable(call: Call<R>): Single<*> {
        return Single.create<Any> { emitter ->
            call.enqueue(object: Callback<R> {
                override fun onFailure(call: Call<R>, t: Throwable) {
                    emitter.onError(t)
                }

                override fun onResponse(call: Call<R>, response: Response<R>) {
                    emitter.onSuccess(response.body()!!)
                }
            })
        }
    }
}
