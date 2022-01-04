package io.bkbn.kompendium.oas

import io.bkbn.kompendium.oas.common.ExternalDocumentation
import io.bkbn.kompendium.oas.common.Tag
import io.bkbn.kompendium.oas.component.Components
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.server.Server
import kotlinx.serialization.Serializable

@Serializable
data class OpenApiSpec(
  val openapi: String = "3.0.3",
  val info: Info,
  val servers: MutableList<Server> = mutableListOf(),
  val paths: MutableMap<String, Path> = mutableMapOf(),
  val components: Components = Components(),
  val security: MutableList<Map<String, List<String>>> = mutableListOf(),
  val tags: MutableList<Tag> = mutableListOf(),
  val externalDocs: ExternalDocumentation? = null
)
