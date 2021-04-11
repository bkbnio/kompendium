package org.leafygreens.kompendium.models

data class OpenApiSpecSecuritySchema(
  val type: String, // TODO Enum? "apiKey", "http", "oauth2", "openIdConnect"
  val name: String,
  val `in`: String,
  val scheme: String,
  val flows: OpenApiSpecOAuthFlows,
  val bearerFormat: String?,
  val description: String?,
)

