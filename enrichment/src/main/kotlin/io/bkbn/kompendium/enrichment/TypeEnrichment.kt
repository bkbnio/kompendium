package io.bkbn.kompendium.enrichment

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

class TypeEnrichment<T>(val id: String) : Enrichment {

  private val enrichments: MutableMap<KProperty1<*, *>, Enrichment> = mutableMapOf()

  fun getEnrichmentForProperty(property: KProperty<*>): Enrichment? = enrichments[property]

  operator fun <R> KProperty1<T, R>.invoke(init: FieldEnrichment.() -> Unit) {
    require(!enrichments.containsKey(this)) { "${this.name} has already been registered" }
    val fieldEnrichment = FieldEnrichment()
    init.invoke(fieldEnrichment)
    enrichments[this] = fieldEnrichment
  }

  companion object {
    inline operator fun <reified T> invoke(id: String, init: TypeEnrichment<T>.() -> Unit): TypeEnrichment<T> {
      val builder = TypeEnrichment<T>(id)
      return builder.apply(init)
    }
  }
}
