package io.bkbn.kompendium.enrichment

data class FieldEnrichment(
  private var deprecated: Boolean = false,
  private var fieldDescription: String? = null,
  private var enrichment: TypeEnrichment<*>? = null,
) : Enrichment {
  fun description(description: String) {
    fieldDescription = description
  }

  fun deprecated() {
    deprecated = true
  }

  fun enrichment(enrichment: TypeEnrichment<*>) {
    this.enrichment = enrichment
  }
}
