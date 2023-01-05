package io.bkbn.kompendium.enrichment

class PropertyEnrichment : Enrichment {
  var deprecated: Boolean? = null
  var description: String? = null
  var typeEnrichment: TypeEnrichment<*>? = null
}
