package io.bkbn.kompendium.core.enrichment

data class FieldEnrichment(
  private var deprecated: Boolean = false,
  private var fieldDescription: String? = null,
) {
  fun description(description: String) {
    fieldDescription = description
  }

  fun deprecated() {
    deprecated = true
  }
}
