package io.bkbn.kompendium.enrichment

class BooleanEnrichment(override val id: String) : Enrichment {
  override var deprecated: Boolean? = null
  override var description: String? = null

  companion object {
    inline operator fun invoke(
      id: String,
      init: BooleanEnrichment.() -> Unit
    ): BooleanEnrichment {
      val builder = BooleanEnrichment(id)
      return builder.apply(init)
    }
  }
}
