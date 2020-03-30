package tech.jorgecastro.jsonapi.converter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import tech.jorgecastro.jsonapi.JsonApiListResponse
import tech.jorgecastro.jsonapi.JsonApiMapper
import tech.jorgecastro.jsonapi.JsonApiResponse
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
        return when (Types.getRawType(type)) {
            Flow::class.java -> {
                if (type is ParameterizedType) {
                    val parameterType = getParameterUpperBound(0, type)
                    getFlowJsonConverter(parameterType) as Converter<ResponseBody, Class<*>>
                }
                else {
                    getFlowJsonConverter(type) as Converter<ResponseBody, Class<*>>
                }
            }
            else -> getJsonConverter(type) as Converter<ResponseBody, Class<*>>
        }
    }

    private fun getJsonConverter(type: Type): JsonConverter {
        /**
         * When is Set of Elements
         */
        if (type is ParameterizedType) {
            var parameterType = getParameterUpperBound(0, type)
            val packageClass = (parameterType as Class<*>).name
            val aClass = Class.forName(packageClass)
            return JsonConverter.getInstance(aClass, getRawType(type))
        }

        return JsonConverter.getInstance(
            Class.forName((type as Class<*>).name)
        )
    }

    private fun getFlowJsonConverter(type: Type): FlowJsonConverter<*> {
        /**
         * When is Set of Elements
         */
        if (type is ParameterizedType) {
            var parameterType = getParameterUpperBound(0, type)

            return FlowJsonConverter.getInstance<Any>(
                Class.forName((parameterType as Class<*>).name),
                getRawType(type)
            )
        }

        return FlowJsonConverter.getInstance<Any>(
            Class.forName((type as Class<*>).name)
        )
    }

    internal class JsonConverter(val classReference: Class<*>, val classReferenceList: Class<*>? = null): Converter<ResponseBody?, Any?> {
        @Throws(Exception::class)
        override fun convert(responseBody: ResponseBody?): Any? {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
           return HandleJsonApiConverter().exec {

               val jsonObject = JSONObject(responseBody?.string())

               if (classReferenceList == null) {
                   val parameterizedType = Types.newParameterizedType(JsonApiResponse::class.java, classReference)
                   val jsonAdapter: JsonAdapter<JsonApiResponse<*>> = moshi.adapter(parameterizedType)
                   val jsonApiObject = jsonAdapter.fromJson(jsonObject.toString())
                   JsonApiMapper().jsonApiMapToListObject(input = jsonApiObject as JsonApiResponse<*>, rawType = classReference.kotlin)
               }
               else {
                   val listType = Types.newParameterizedType(JsonApiListResponse::class.java, classReference)
                   val jsonAdapter: JsonAdapter<JsonApiListResponse<*>> = moshi.adapter(listType)
                   val jsonApiObject = jsonAdapter.fromJson(jsonObject.toString())
                   JsonApiMapper().jsonApiMapToListObject(input = jsonApiObject as JsonApiListResponse<*>, rawType = classReference.kotlin)
               }
            }
        }

        companion object {
            fun getInstance(classReference: Class<*>, classReferenceList: Class<*>? = null) =
                JsonConverter(
                    classReference,
                    classReferenceList
                )
        }
    }


    internal class FlowJsonConverter<T>(val classReference: Class<*>, val classReferenceList: Class<*>? = null): Converter<ResponseBody?, Flow<*>?> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody?): Flow<*> {

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            return flow<Any> {
                emit(
                    suspendCancellableCoroutine { cancellableContinuation ->

                        /**
                         * val jsonObject = JSONObject(responseBody?.string()).toString()
                        val responseObject = Moshi.Builder().build().adapter().fromJson(jsonObject)
                         */
                        val jsonObject = JSONObject(responseBody?.string())


                        if (classReferenceList == null) {
                            val parameterizedType = Types.newParameterizedType(JsonApiResponse::class.java, classReference)
                            val jsonAdapter: JsonAdapter<JsonApiResponse<*>> = moshi.adapter(parameterizedType)
                            val jsonApiObject = jsonAdapter.fromJson(jsonObject.toString())
                            val response = JsonApiMapper().jsonApiMapToListObject(input = jsonApiObject as JsonApiResponse<*>, rawType = classReference.kotlin)
                            cancellableContinuation.resume(response as Any)
                        }
                        else {

                            val listType = Types.newParameterizedType(JsonApiListResponse::class.java, classReference)
                            val jsonAdapter: JsonAdapter<JsonApiListResponse<*>> = moshi.adapter(listType)
                            val jsonApiObject = jsonAdapter.fromJson(jsonObject.toString())

                            JsonApiMapper().jsonApiMapToListObject(input = jsonApiObject as JsonApiListResponse, rawType = classReference.kotlin)?.let {
                                cancellableContinuation.resume(it)
                            }
                        }
                    }
                )
            }
        }

        companion object {
            fun <T>getInstance(classReference: Class<*>, classReferenceList: Class<*>? = null) =
                FlowJsonConverter<T>(
                    classReference = classReference,
                    classReferenceList = classReferenceList
                )
        }
    }
}

fun<T: Any> T.getClass(): KClass<T> {
    return javaClass.kotlin
}