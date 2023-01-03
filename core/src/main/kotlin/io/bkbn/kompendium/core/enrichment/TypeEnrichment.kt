package io.bkbn.kompendium.core.enrichment

import kotlin.reflect.KProperty1

class TypeEnrichment<T : Any> {

  private val fieldEnrichments: MutableMap<KProperty1<*, *>, FieldEnrichment> = mutableMapOf()

  operator fun <R> KProperty1<T, R>.invoke(init: FieldEnrichment.() -> Unit) {
    require(!fieldEnrichments.containsKey(this)) { "${this.name} has already been registered" }
    val fieldEnrichment = FieldEnrichment()
    init.invoke(fieldEnrichment)
    fieldEnrichments[this] = fieldEnrichment
  }

  fun getFieldEnrichments() = fieldEnrichments.toMap()

  companion object {
    inline operator fun <reified T : Any> invoke(init: TypeEnrichment<T>.() -> Unit): TypeEnrichment<T> {
      val builder = TypeEnrichment<T>()
      return builder.apply(init)
    }
  }
}

data class CoolStuff(val a: String, val b: Int)
data class MoreStuff(val c: Float, val d: CoolStuff)

fun main() {
  TypeEnrichment {
    CoolStuff::a {
      deprecated()
    }
    CoolStuff::b {
      description("This is an important number")
    }
  }
}
