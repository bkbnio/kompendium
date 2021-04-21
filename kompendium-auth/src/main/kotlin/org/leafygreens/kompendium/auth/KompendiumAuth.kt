package org.leafygreens.kompendium.auth

import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.auth.BasicAuthenticationProvider
import io.ktor.auth.jwt.jwt
import io.ktor.auth.jwt.JWTAuthenticationProvider
import org.leafygreens.kompendium.Kompendium
import org.leafygreens.kompendium.models.oas.OpenApiSpecSchemaSecurity

object KompendiumAuth {

  init {
      Kompendium.pathCalculator = AuthPathCalculator()
  }

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

  // TODO move jwt to separate module?
  fun Authentication.Configuration.notarizedJwt(
    name: String? = null,
    header: String? = null,
    scheme: String? = null,
    configure: JWTAuthenticationProvider.Configuration.() -> Unit
  ) {
    if (header == null || header == "Authorization") {
      Kompendium.openApiSpec.components.securitySchemes[name ?: "default"] = OpenApiSpecSchemaSecurity(
        type = "http",
        scheme = scheme ?: "bearer"
      )
    } else {
      Kompendium.openApiSpec.components.securitySchemes[name ?: "default"] = OpenApiSpecSchemaSecurity(
        type = "apiKey",
        name = header,
        `in` = "header"
      )
    }
    jwt(name, configure)
  }

  // TODO support other authentication providers (e.g., oAuth)?
}
