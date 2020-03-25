package tech.jorgecastro.jsonapi.adapter


import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class JsonApiRxJava2CallAdapter<R>(
    private val responseType: Type
): CallAdapter<R, Any> {
    override fun adapt(call: Call<R>): Any {
        return Single.create<Any> { singleEmitter ->
            call.enqueue(object: Callback<R> {
                override fun onFailure(call: Call<R>, t: Throwable) {
                    singleEmitter.onError(t)
                }

                override fun onResponse(call: Call<R>, response: Response<R>) {
                    singleEmitter.onSuccess(response.body()!!)
                }

            })
        }
    }

    override fun responseType() =  responseType

}