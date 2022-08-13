package io.bkbn.kompendium.core.attribute

import io.bkbn.kompendium.oas.OpenApiSpec
import io.ktor.util.AttributeKey

object KompendiumAttributes {
  val openApiSpec = AttributeKey<OpenApiSpec>("OpenApiSpec")
}
