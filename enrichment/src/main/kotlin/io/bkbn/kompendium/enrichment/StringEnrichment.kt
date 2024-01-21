package io.bkbn.kompendium.enrichment

class StringEnrichment(override val id: String): Enrichment {
  override var deprecated: Boolean? = null
  override var description: String? = null

  var maxLength: Int? = null
  var minLength: Int? = null
  var pattern: String? = null
  var contentEncoding: String? = null
  var contentMediaType: String? = null
  // TODO how to handle contentSchema?
}
