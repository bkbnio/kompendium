package io.bkbn.kompendium.auth

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.oas.security.SecuritySchema
import io.ktor.server.application.createApplicationPlugin

object NotarizedAuthentication {

  class Config {
    var securitySchemes: Map<String, SecuritySchema> = mapOf()
  }

  operator fun invoke() = createApplicationPlugin(
    name = "NotarizedAuthentication",
    createConfiguration = ::Config
  ) {
    val spec = application.attributes[KompendiumAttributes.openApiSpec]
    pluginConfig.securitySchemes.forEach { (k, v) -> spec.components.securitySchemes[k] = v }
  }
}
