package tech.jorgecastro.jsonapi.commons

import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Kotlin extension for javaClass.declaredFields abbreviation
 *
 * @return array of [Field]
 */
fun Any.jDeclaredFields(): Array<out Field> {
    return javaClass?.declaredFields
}

fun Field.setWithIgnorePrivateCase(obj: Any?, value: Any?) {
    isAccessible = true
    set(obj, value)
    isAccessible = false
}

fun<T: Any> T.getClass(): KClass<T> {
    return javaClass.kotlin
}
