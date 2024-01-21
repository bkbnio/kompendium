package io.bkbn.kompendium.enrichment

sealed interface Enrichment {
  val id: String
  var deprecated: Boolean?
  var description: String?
}
