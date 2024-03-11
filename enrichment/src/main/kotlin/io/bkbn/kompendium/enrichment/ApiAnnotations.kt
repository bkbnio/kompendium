package io.bkbn.kompendium.enrichment

import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.PROPERTY

@Target(PROPERTY)
annotation class ApiProperty(
  val description: String,
  val deprecated: Boolean = false,
)

@Target(PROPERTY)
annotation class ApiString(
  val description: String,
  val deprecated: Boolean = false,
  val maxLength: Int = Int.MAX_VALUE,
  val minLength: Int = 0,
)

@Target(PROPERTY)
annotation class ApiInt(
  val description: String,
  val deprecated: Boolean = false,
  val minValue: Int = Int.MIN_VALUE,
  val maxValue: Int = Int.MAX_VALUE,
)

@Target(PROPERTY)
annotation class ApiList(
  val description: String,
  val deprecated: Boolean = false,
  val minItems: Int = 0,
  val maxItems: Int = Int.MAX_VALUE,
)

@Target(CLASS)
annotation class ApiClass(
  val refId: String
)

fun ApiString.toEnrichment() = PropertyEnrichment().apply {
  description = this@toEnrichment.description
  deprecated = this@toEnrichment.deprecated
  maxLength = this@toEnrichment.maxLength
  minLength = this@toEnrichment.minLength
}

fun ApiInt.toEnrichment() = PropertyEnrichment().apply {
  description = this@toEnrichment.description
  deprecated = this@toEnrichment.deprecated
  maximum = this@toEnrichment.maxValue
  minimum = this@toEnrichment.minValue
}

fun ApiList.toEnrichment() = PropertyEnrichment().apply {
  description = this@toEnrichment.description
  deprecated = this@toEnrichment.deprecated
  maxItems = this@toEnrichment.maxItems
  minItems = this@toEnrichment.minItems
}

fun ApiProperty.toEnrichment() = PropertyEnrichment().apply {
  description = this@toEnrichment.description
  deprecated = this@toEnrichment.deprecated
}
