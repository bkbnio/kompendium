package io.bkbn.kompendium.oas.schema

data class SecuritySchema(
  val type: String? = null, // TODO Enum? "apiKey", "http", "oauth2", "openIdConnect"
  val name: String? = null,
  val `in`: String? = null,
  val scheme: String? = null,
//  val flows: OpenApiSpecOAuthFlows? = null, TODO This is totally wrong
  val bearerFormat: String? = null,
  val description: String? = null,
)
