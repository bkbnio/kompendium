package io.bkbn.kompendium.enrichment

class BooleanEnrichment(override val id: String): Enrichment {
  override var deprecated: Boolean? = null
  override var description: String? = null
}
