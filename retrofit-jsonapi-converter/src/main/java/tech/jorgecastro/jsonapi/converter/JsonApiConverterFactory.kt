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
import tech.jorgecastro.jsonapi.JsonApiResource
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
    ): Converter<ResponseBody, Class<*>>? {

        /**
         * Se verifica que que el objeto a convertir tenga la anotación JsonApiResource de lo contrario
         * se ignora el adaptador JsonApiConverterFactory
         */
        val rawType = getRawType(type)
        check(checkIsJsonApiResourceAnnotation(type)) { return null }

        return when (rawType) {
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

    private fun checkIsJsonApiResourceAnnotation(type: Type): Boolean {
        var isJsonApi: Boolean
        var objectClass: Class<*>
        if (type is ParameterizedType) {
            var newType = getParameterUpperBound(0, type)
            while (newType is ParameterizedType) { newType = getParameterUpperBound(0, newType) }
            objectClass = Class.forName(getRawType(newType).name)
        }
        else {
            objectClass = Class.forName(getRawType(type).name)
        }

        isJsonApi = objectClass.annotations.filterIsInstance<JsonApiResource>().count() > 0
        return isJsonApi
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

    private fun getFlowJsonConverter(type: Type): FlowJsonConverter {
        /**
         * When is Set of Elements
         */
        if (type is ParameterizedType) {
            var parameterType = getParameterUpperBound(0, type)

            return FlowJsonConverter.getInstance(
                Class.forName((parameterType as Class<*>).name),
                getRawType(type)
            )
        }
        return FlowJsonConverter.getInstance(Class.forName((type as Class<*>).name))
    }


    internal class JsonConverter(val classReference: Class<*>, val classReferenceList: Class<*>? = null): Converter<ResponseBody?, Any?> {
        @Throws(Exception::class)
        override fun convert(responseBody: ResponseBody?): Any? {
           return HandleJsonApiConverter().exec {
               if (classReferenceList == null)
                   getJsonApiResponseObject(responseBody, classReference)
               else
                   getJsonApiResponseList(responseBody, classReference)
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


    internal class FlowJsonConverter(val classReference: Class<*>, val classReferenceList: Class<*>? = null): Converter<ResponseBody?, Flow<*>?> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody?): Flow<*> {
            return flow<Any> {
                emit(
                    suspendCancellableCoroutine { cancellableContinuation ->
                        if (classReferenceList == null) {
                            val response = getJsonApiResponseObject(responseBody,classReference)
                            cancellableContinuation.resume(response as Any)
                        }
                        else {
                            val jsonApiResponseList = getJsonApiResponseList(responseBody, classReference)
                            jsonApiResponseList?.let {
                                cancellableContinuation.resume(it)
                            }
                        }
                    }
                )
            }
        }

        companion object {
            fun getInstance(classReference: Class<*>, classReferenceList: Class<*>? = null) =
                FlowJsonConverter(
                    classReference = classReference,
                    classReferenceList = classReferenceList
                )
        }
    }



    companion object {
        fun getJsonApiResponseObject(responseBody: ResponseBody?, classReference: Class<*>): Any? {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonObject = JSONObject(responseBody?.string())
            val parameterizedType = Types.newParameterizedType(JsonApiResponse::class.java, classReference)
            val jsonAdapter: JsonAdapter<JsonApiResponse<*>> = moshi.adapter(parameterizedType)
            val jsonApiObject = jsonAdapter.fromJson(jsonObject.toString())
            return JsonApiMapper().jsonApiMapToListObject(input = jsonApiObject as JsonApiResponse<*>, rawType = classReference.kotlin)
        }

        fun getJsonApiResponseList(responseBody: ResponseBody?, classReference: Class<*>): List<Any>? {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonObject = JSONObject(responseBody?.string())
            val listType = Types.newParameterizedType(JsonApiListResponse::class.java, classReference)
            val jsonAdapter: JsonAdapter<JsonApiListResponse<*>> = moshi.adapter(listType)
            val jsonApiObject = jsonAdapter.fromJson(jsonObject.toString())
            return JsonApiMapper().jsonApiMapToListObject(input = jsonApiObject as JsonApiListResponse<*>, rawType = classReference.kotlin)
        }
    }
}
