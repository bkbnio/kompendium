package org.leafygreens.kompendium.models.meta

data class MethodInfo(
  val summary: String,
  val description: String? = null,
  val tags: Set<String> = emptySet(),
  val deprecated: Boolean = false
)
