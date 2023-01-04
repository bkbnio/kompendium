package io.bkbn.kompendium.enrichment

class PropertyEnrichment : Enrichment {
  var deprecated: Boolean? = null
  var fieldDescription: String? = null
  var typeEnrichment: TypeEnrichment<*>? = null
}
