package io.bkbn.kompendium.enrichment

import kotlin.annotation.AnnotationTarget.PROPERTY

@Target(PROPERTY)
annotation class ApiDoc(
  val description: String,
  val examples: Array<String> = []
)
