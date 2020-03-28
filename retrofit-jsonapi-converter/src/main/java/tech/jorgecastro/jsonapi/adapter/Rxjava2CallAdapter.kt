package tech.jorgecastro.jsonapi.adapter

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class JsonApiRxJava2CallAdapter<R>(
    private val responseType: Type,
    private val rawType: Class<*>
): CallAdapter<R, Any> {
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
                    emitter.onNext(response.body()!!)
                    emitter.onComplete()
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