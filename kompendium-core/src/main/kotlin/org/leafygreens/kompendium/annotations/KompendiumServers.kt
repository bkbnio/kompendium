package org.leafygreens.kompendium.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class KompendiumServers(
  val urls: Array<String>
)
