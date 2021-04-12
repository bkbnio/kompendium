package org.leafygreens.kompendium.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class KompendiumContact(
  val name: String,
  val url: String = "",
  val email: String = ""
)
