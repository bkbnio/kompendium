package org.leafygreens.kompendium.annotations

annotation class KompendiumRequest(
  val description: String,
  val required: Boolean = true,
  val mediaTypes: Array<String> = ["application/json"]
)
