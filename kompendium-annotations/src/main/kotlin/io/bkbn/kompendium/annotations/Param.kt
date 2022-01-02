package io.bkbn.kompendium.annotations

/**
 * Used to indicate that a field in a data class represents an OpenAPI parameter
 * @param type The type of parameter, must be valid [ParamType]
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Param(val type: ParamType)
