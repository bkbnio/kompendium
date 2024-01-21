package io.bkbn.kompendium.enrichment

class MapEnrichment<V>(override val id: String) : TypeEnrichment<V> {

  override var deprecated: Boolean? = null
  override var description: String? = null

  var maxProperties: Int? = null
  var minProperties: Int? = null

  lateinit var keyEnrichment: StringEnrichment
  lateinit var valueEnrichment: TypeEnrichment<*>

  companion object {
    inline operator fun <reified V> invoke(
      id: String,
      init: MapEnrichment<V>.() -> Unit
    ): MapEnrichment<V> {
      val builder = MapEnrichment<V>(id)
      return builder.apply(init)
    }
  }
}
