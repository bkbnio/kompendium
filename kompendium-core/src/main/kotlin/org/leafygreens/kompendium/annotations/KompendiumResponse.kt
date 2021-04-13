package org.leafygreens.kompendium.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KompendiumResponse(
  val status: Int,
  val description: String,
  val mediaTypes: Array<String> = ["application/json"]
)
