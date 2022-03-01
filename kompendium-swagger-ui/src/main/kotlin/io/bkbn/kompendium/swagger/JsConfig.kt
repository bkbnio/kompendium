package io.bkbn.kompendium.swagger

import java.net.URI

// This class represents this specification:
// https://swagger.io/docs/open-source-tools/swagger-ui/usage/configuration/
data class JsConfig(
  val specs: Map<String, URI>,
  val deepLinking: Boolean = true,
  val displayOperationId: Boolean = true,
  val displayRequestDuration: Boolean = true,
  val docExpansion: String = "none",
  val operationsSorter: String = "alpha",
  val defaultModelExpandDepth: Int = 4,
  val defaultModelsExpandDepth: Int = 4,
  val persistAuthorization: Boolean = true,
  val tagsSorter: String = "alpha",
  val tryItOutEnabled: Boolean = false,
  val validatorUrl: String? = null,
  val jsInit: () -> String? = { null }
)

internal fun JsConfig.toJsProps(): String = asMap()
  .filterKeys { !setOf("specs", "jsInit").contains(it) }
  .map{ "${it.key}: ${it.value.toJs()}" }
  .joinToString(separator = ",\n\t\t")

internal fun JsConfig.getSpecUrlsProps(): String =
  if (specs.isEmpty()) "[]" else specs.map { "{url: ${it.value.toJs()}, name: ${it.key.toJs()}}" }.toString()
