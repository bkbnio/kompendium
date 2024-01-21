package io.bkbn.kompendium.enrichment

class MapEnrichment<K, V>(override val id: String) : TypeEnrichment<V> {

  override var deprecated: Boolean? = null
  override var description: String? = null

  var maxProperties: Int? = null
  var minProperties: Int? = null

  lateinit var keyEnrichment: TypeEnrichment<*>
  lateinit var valueEnrichment: TypeEnrichment<*>

  companion object {
    inline operator fun <reified V, reified K> invoke(
      id: String,
      init: MapEnrichment<K, V>.() -> Unit
    ): MapEnrichment<K, V> {
      val builder = MapEnrichment<K, V>(id)
      return builder.apply(init)
    }
  }
}
