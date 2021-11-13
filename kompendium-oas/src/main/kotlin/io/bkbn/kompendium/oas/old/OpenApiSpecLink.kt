package io.bkbn.kompendium.oas.old

data class OpenApiSpecLink(
  val operationRef: String?, // todo mutually exclusive with operationId
  val operationId: String?,
  val parameters: Map<String, String>, // todo sheesh https://swagger.io/specification/#link-object
  val requestBody: String, // todo same
  val description: String?,
  val server: OpenApiSpecServer?
)
