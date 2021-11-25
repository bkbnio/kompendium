package io.bkbn.kompendium.annotations

/**
 * Used to indicate that a field in a data class represents an OpenAPI parameter
 * @param type The type of parameter, must be valid [ParamType]
 * @param description Description of the parameter to include in OpenAPI Spec
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class KompendiumParam(val type: ParamType, val description: String = "")

enum class ParamType {
  COOKIE,
  HEADER,
  PATH,
  QUERY
}
