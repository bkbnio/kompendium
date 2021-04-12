package org.leafygreens.kompendium.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class KompendiumLicense(
  val name: String,
  val url: String = ""
)
