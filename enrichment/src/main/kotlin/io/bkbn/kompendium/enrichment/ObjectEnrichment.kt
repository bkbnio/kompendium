package io.bkbn.kompendium.enrichment

import kotlin.reflect.KProperty1

class ObjectEnrichment<T>(override val id: String) : TypeEnrichment<T> {

  override var deprecated: Boolean? = null
  override var description: String? = null

  private val _propertyEnrichments: MutableMap<KProperty1<*, *>, Enrichment> = mutableMapOf()

  val propertyEnrichment: Map<KProperty1<*, *>, Enrichment>
    get() = _propertyEnrichments.toMap()

  operator fun <R> KProperty1<T, R>.invoke(init: () -> Enrichment) {
    require(!_propertyEnrichments.containsKey(this)) { "${this.name} has already been registered" }
    val enrichment = init.invoke()
    _propertyEnrichments[this] = enrichment
  }

  companion object {
    inline operator fun <reified T> invoke(id: String, init: ObjectEnrichment<T>.() -> Unit): ObjectEnrichment<T> {
      val builder = ObjectEnrichment<T>(id)
      return builder.apply(init)
    }
  }
}
