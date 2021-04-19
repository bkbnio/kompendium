package org.leafygreens.kompendium

import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.routing.*
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaSecurity

object KompendiumAuth {

  fun Authentication.Configuration.notarizedBasic(
    name: String? = null,
    configure: BasicAuthenticationProvider.Configuration.() -> Unit
  ) {
    Kompendium.openApiSpec.components.securitySchemes[name ?: "default"] = OpenApiSpecSchemaSecurity(
      type = "http",
      scheme = "basic"
    )
    basic(name, configure)
  }

  // TODO: move jwt to separate module?
  fun Authentication.Configuration.notarizedJwt(
    name: String? = null,
    // TODO: add support if other header than Authorization or other scheme than Bearer is used
    configure: JWTAuthenticationProvider.Configuration.() -> Unit
  ) {
    Kompendium.openApiSpec.components.securitySchemes[name ?: "default"] = OpenApiSpecSchemaSecurity(
      type = "http",
      scheme = "bearer"
    )
    jwt(name, configure)
  }

  // TODO: support other authentication providers (e.g., oAuth)?

  fun Route.notarizedAuthentication(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    build: Route.() -> Unit
  ) {
    // TODO: support multiple security schemes
    val previousSchemeName = Kompendium.currentSecuritySchemeName
    Kompendium.currentSecuritySchemeName =
      if (configurations.isEmpty())
        "default"
      else configurations.first { Kompendium.openApiSpec.components.securitySchemes[it] != null }

    require(Kompendium.openApiSpec.components.securitySchemes[Kompendium.currentSecuritySchemeName] != null) {
      "No notarized security scheme found with name ${Kompendium.currentSecuritySchemeName}"
    }

    authenticate(*configurations, optional = optional, build = build)
    Kompendium.currentSecuritySchemeName = previousSchemeName
  }
}
