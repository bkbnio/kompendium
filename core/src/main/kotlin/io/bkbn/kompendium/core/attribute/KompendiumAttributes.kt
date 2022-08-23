package io.bkbn.kompendium.core.attribute

import io.bkbn.kompendium.json.schema.SchemaConfigurator
import io.bkbn.kompendium.oas.OpenApiSpec
import io.ktor.util.AttributeKey

object KompendiumAttributes {
  val openApiSpec = AttributeKey<OpenApiSpec>("OpenApiSpec")
  val schemaConfigurator = AttributeKey<SchemaConfigurator>("SchemaConfigurator")
}
