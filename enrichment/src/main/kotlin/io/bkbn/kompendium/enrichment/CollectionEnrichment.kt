package io.bkbn.kompendium.enrichment

class CollectionEnrichment<T>(override val id: String) : TypeEnrichment<T> {

  override var deprecated: Boolean? = null
  override var description: String? = null

  var maxItems: Int? = null
  var minItems: Int? = null
  var uniqueItems: Boolean? = null
  // TODO How to handle contains, minContains, maxContains?

  var itemEnrichment: TypeEnrichment<*>? = null

  companion object {
    inline operator fun <reified T> invoke(
      id: String,
      init: CollectionEnrichment<T>.() -> Unit
    ): CollectionEnrichment<T> {
      val builder = CollectionEnrichment<T>(id)
      return builder.apply(init)
    }
  }
}
