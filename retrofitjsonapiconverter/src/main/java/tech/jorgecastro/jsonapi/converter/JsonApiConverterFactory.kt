package tech.jorgecastro.jsonapi.converter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.JsonApiListResponse
import tech.jorgecastro.jsonapi.JsonApiMapper
import tech.jorgecastro.jsonapi.dto.ZoneCoverage
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.reflect.KClass



class JsonApiConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, Class<*>> {
        //return JsonConverter.INSTANCE as Converter<ResponseBody, *>

        if (type is ParameterizedType) {
            if (Types.getRawType(type) == Flow::class.java) {
                var parameterType: Type = getParameterUpperBound(0, type)

                /**
                 * When is Set of Elements
                 */
                if (parameterType is ParameterizedType) {
                    parameterType = getParameterUpperBound(0, parameterType)
                }

                return FlowJsonConverter.getInstance<Any>(
                    Class.forName((parameterType as Class<*>).name)
                ) as Converter<ResponseBody, Class<*>>
            }
        }

        return JsonConverter.getInstance(
            Class.forName((type as Class<*>).name)
        ) as Converter<ResponseBody, Class<*>>
    }

    internal class JsonConverter(val classReference: Class<*>): Converter<ResponseBody?, Any?> {
        @Throws(Exception::class)
        override fun convert(responseBody: ResponseBody?): Any? {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
           return HandleJsonApiConverter().exec {
               val jsonObject = JSONObject(responseBody?.string())
               moshi.adapter(classReference).fromJson(jsonObject.toString())
            }
        }

        companion object {
            fun getInstance(classReference: Class<*>) =
                JsonConverter(
                    classReference
                )
        }
    }


    internal class FlowJsonConverter<T>(val classReference: Class<*>): Converter<ResponseBody?, Flow<List<*>>?> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody?): Flow<List<*>> {

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            return flow<List<*>> {
                emit(
                    suspendCancellableCoroutine { cancellableContinuation ->


                            // val jsonObject = JSONObject(responseBody?.string())
                            /**
                             * val jsonObject = JSONObject(responseBody?.string()).toString()
                            val responseObject = Moshi.Builder().build().adapter().fromJson(jsonObject)
                             */
                            val jsonObject = JSONObject(responseBody?.string()).toString()

                            val listType = Types.newParameterizedType(JsonApiListResponse::class.java, classReference)
                            val jsonAdapter: JsonAdapter<JsonApiListResponse<ZoneCoverage>> = moshi.adapter(listType)
                            val jsonApiObject = jsonAdapter.fromJson(jsonObject)
                            //val responseObject = moshi.adapter(ZoneCoverage::class.java).fromJson("{\"country_name\":\"Colombia\",\"city_name\":\"Bogotá, D.C.\"}")
                            //val responseObject = moshi.adapter(classReference).fromJson(a.toString())

                            JsonApiMapper().jsonApiMapToListObject<ZoneCoverage>(input = jsonApiObject!!)?.let {
                                cancellableContinuation.resume(it)
                            }

                            //requireNotNull(jsonApiObject) { "responseObject can't be null" }
                            //cancellableContinuation.resume(jsonApiObject)
                    }
                )
            }

            /*return try {

                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                return HandleJsonApiConverter().exec {
                    // val jsonObject = JSONObject(responseBody?.string())
                    /**
                     * val jsonObject = JSONObject(responseBody?.string()).toString()
                    val responseObject = Moshi.Builder().build().adapter().fromJson(jsonObject)
                     */
                    val a = JSONObject(responseBody?.string())
                    //val jsonObject = JSONObject(responseBody?.string()).toString()
                    //val responseObject = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(ZoneCoverage::class.java).fromJson("{\"country_name\":\"Colombia\",\"city_name\":\"Bogotá, D.C.\"}")
                    val responseObject = moshi.adapter(classReference).fromJson(a.toString())
                    flow { emit(responseObject as ZoneCoverage) }
                } as Flow<*>
            } catch (e: JSONException) {
                throw IOException("Failed to parse JSON", e)
            }*/
        }

        companion object {
            //val INSTANCE = JsonConverter()
            fun <T>getInstance(classReference: Class<*>) =
                FlowJsonConverter<T>(
                    classReference
                )
        }
    }
}

fun<T: Any> T.getClass(): KClass<T> {
    return javaClass.kotlin
}