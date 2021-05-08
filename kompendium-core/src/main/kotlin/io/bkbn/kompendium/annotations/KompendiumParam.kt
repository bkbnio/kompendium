package io.bkbn.kompendium.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class KompendiumParam(val type: ParamType, val description: String = "")

enum class ParamType {
  COOKIE,
  HEADER,
  PATH,
  QUERY
}
