package org.leafygreens.kompendium.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class KompendiumInfo(
  val title: String,
  val version: String,
  val description: String = "",
  val termsOfService: String = ""
)
