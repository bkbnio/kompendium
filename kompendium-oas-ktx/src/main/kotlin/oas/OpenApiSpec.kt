package oas

import oas.common.ExternalDocumentation
import oas.common.Tag
import oas.component.Components
import oas.info.Info
import oas.path.Path
import oas.server.Server

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
