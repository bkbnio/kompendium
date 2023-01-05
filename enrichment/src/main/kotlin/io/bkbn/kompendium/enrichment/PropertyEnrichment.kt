package io.bkbn.kompendium.enrichment

/**
 * Reference https://json-schema.org/draft/2020-12/json-schema-validation.html#name-multipleof
 */
class PropertyEnrichment : Enrichment {
  // Metadata
  var deprecated: Boolean? = null
  var description: String? = null
  var typeEnrichment: TypeEnrichment<*>? = null

  // Number and Integer Constraints
  var multipleOf: Number? = null
  var maximum: Number? = null
  var exclusiveMaximum: Number? = null
  var minimum: Number? = null
  var exclusiveMinimum: Number? = null

  // String constraints
  var maxLength: Int? = null
  var minLength: Int? = null
  var pattern: String? = null
  var contentEncoding: String? = null
  var contentMediaType: String? = null
  // TODO how to handle contentSchema?

  // Array constraints
  var maxItems: Int? = null
  var minItems: Int? = null
  var uniqueItems: Boolean? = null
  // TODO How to handle contains, minContains, maxContains?

  // Object constraints
  var maxProperties: Int? = null
  var minProperties: Int? = null
}
