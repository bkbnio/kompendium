package io.bkbn.kompendium.enrichment
class NumberEnrichment(override val id: String) : Enrichment {

  override var deprecated: Boolean? = null
  override var description: String? = null

  var multipleOf: Number? = null
  var maximum: Number? = null
  var exclusiveMaximum: Number? = null
  var minimum: Number? = null
  var exclusiveMinimum: Number? = null

  companion object {
    inline operator fun invoke(id: String, init: NumberEnrichment.() -> Unit): NumberEnrichment {
      val builder = NumberEnrichment(id)
      return builder.apply(init)
    }
  }
}
