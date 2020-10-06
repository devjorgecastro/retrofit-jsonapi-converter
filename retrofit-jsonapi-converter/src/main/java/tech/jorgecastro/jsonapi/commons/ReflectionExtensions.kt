package tech.jorgecastro.jsonapi.commons

import java.lang.reflect.Field

/**
 * Kotlin extension for javaClass.declaredFields abbreviation
 *
 * @return array of [Field]
 */
fun Any.jDeclaredFields(): Array<out Field> {
    return javaClass?.declaredFields
}