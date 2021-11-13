package io.bkbn.kompendium.oas.old

data class OpenApiSpecSchemaSecurity(
  val type: String? = null, // TODO Enum? "apiKey", "http", "oauth2", "openIdConnect"
  val name: String? = null,
  val `in`: String? = null,
  val scheme: String? = null,
  val flows: OpenApiSpecOAuthFlows? = null,
  val bearerFormat: String? = null,
  val description: String? = null,
)
