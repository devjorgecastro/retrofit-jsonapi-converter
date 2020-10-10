package tech.jorgecastro.jsonapi.converter

import com.squareup.moshi.JsonDataException
import org.json.JSONException
import tech.jorgecastro.jsonapi.exception.JsonException
import tech.jorgecastro.jsonapi.exception.JsonUnsupportedClass

typealias HandleJsonApiConvertType = () -> Any?

class HandleJsonApiConverter {
    @Throws(Exception::class)
    fun exec(lambda: HandleJsonApiConvertType): Any? {
        return try {
            lambda()
        } catch (e: JSONException) {
            throw JsonException(
                "Failed to parse JSON",
                e
            )
        }
        catch (e: JsonDataException) {
            val errorMessage = e.message ?: "Unsupported class for conversion"
            throw JsonUnsupportedClass(
                errorMessage,
                e
            )
        }
    }
}
