package io.bkbn.kompendium.enrichment

class PropertyEnrichment : Enrichment {
  var deprecated: Boolean = false
  var fieldDescription: String? = null
  var typeEnrichment: TypeEnrichment<*>? = null
}
