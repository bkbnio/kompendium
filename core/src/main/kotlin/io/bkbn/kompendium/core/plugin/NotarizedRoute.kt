package io.bkbn.kompendium.core.plugin

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.util.Helpers.getReferenceSlug
import io.bkbn.kompendium.core.util.Helpers.getSimpleSlug
import io.bkbn.kompendium.json.schema.ReferenceSchema
import io.bkbn.kompendium.json.schema.SchemaGenerator
import io.bkbn.kompendium.oas.path.Path
import io.bkbn.kompendium.oas.path.PathOperation
import io.bkbn.kompendium.oas.payload.MediaType
import io.bkbn.kompendium.oas.payload.Parameter
import io.bkbn.kompendium.oas.payload.Request
import io.bkbn.kompendium.oas.payload.Response
import io.ktor.server.application.createRouteScopedPlugin

@Suppress("MagicNumber") // 🚨 obviously remove
object NotarizedRoute {

  class Config {
    // TODO Would be preferable to introspect from installation path
    lateinit var path: String
    var tags: Set<String> = emptySet()
    var parameters: List<Parameter> = emptyList()
    var get: GetInfo? = null
    var post: PostInfo? = null
    // todo get, post, put, etc.
  }

  operator fun invoke() = createRouteScopedPlugin(
    name = "NotarizedRoute",
    createConfiguration = ::Config
  ) {
    val spec = application.attributes[KompendiumAttributes.openApiSpec]

    require(spec.paths[pluginConfig.path] == null) {
      """
        The specified path ${pluginConfig.path} has already been documented!
        Please make sure that all notarized paths are unique
      """.trimIndent()
    }

    val path = Path()
    path.parameters = pluginConfig.parameters

    pluginConfig.get?.let { get ->
      spec.components.schemas[get.response.responseType.getSimpleSlug()] =
        SchemaGenerator.fromType(get.response.responseType)
      path.get = PathOperation(
        tags = pluginConfig.tags.plus(get.tags),
        summary = get.summary,
        description = get.description,
        externalDocs = get.externalDocumentation,
        operationId = get.operationId,
        deprecated = get.deprecated,
        parameters = get.parameters,
        responses = mapOf(
          get.response.responseCode.value to Response(
            description = get.response.description,
            content = mapOf(
              "application/json" to MediaType(
                schema = ReferenceSchema(get.response.responseType.getReferenceSlug())
              )
            )
          )
        )
      )
    }

    pluginConfig.post?.let { post ->
      spec.components.schemas[post.response.responseType.getSimpleSlug()] =
        SchemaGenerator.fromType(post.response.responseType)
      spec.components.schemas[post.request.requestType.getSimpleSlug()] =
        SchemaGenerator.fromType(post.request.requestType)

      path.post = PathOperation(
        tags = pluginConfig.tags.plus(post.tags),
        summary = post.summary,
        description = post.description,
        externalDocs = post.externalDocumentation,
        operationId = post.operationId,
        deprecated = post.deprecated,
        parameters = post.parameters,
        requestBody = Request(
          description = post.request.description,
          content = mapOf(
            "application/json" to MediaType(
              schema = ReferenceSchema(post.request.requestType.getReferenceSlug())
            )
          ),
          required = true
        ),
        responses = mapOf(
          post.response.responseCode.value to Response(
            description = post.response.description,
            content = mapOf(
              "application/json" to MediaType(
                schema = ReferenceSchema(post.response.responseType.getReferenceSlug())
              )
            )
          )
        )
      )
    }

    // todo handle get, post, put, etc.

    spec.paths[pluginConfig.path] = path
  }
}
