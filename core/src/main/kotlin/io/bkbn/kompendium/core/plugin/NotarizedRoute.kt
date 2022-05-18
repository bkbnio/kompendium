package io.bkbn.kompendium.core.plugin

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.metadata.PutInfo
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
    var put: PutInfo? = null
    var delete: DeleteInfo? = null
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
      path.get = get.toPathOperation(pluginConfig)
    }

    pluginConfig.post?.let { post ->
      spec.components.schemas[post.response.responseType.getSimpleSlug()] =
        SchemaGenerator.fromType(post.response.responseType)
      spec.components.schemas[post.request.requestType.getSimpleSlug()] =
        SchemaGenerator.fromType(post.request.requestType)

      path.post = post.toPathOperation(pluginConfig)
    }

    pluginConfig.put?.let { put ->
      spec.components.schemas[put.response.responseType.getSimpleSlug()] =
        SchemaGenerator.fromType(put.response.responseType)
      spec.components.schemas[put.request.requestType.getSimpleSlug()] =
        SchemaGenerator.fromType(put.request.requestType)

      path.put = put.toPathOperation(pluginConfig)
    }

    pluginConfig.delete?.let { delete ->
      spec.components.schemas[delete.response.responseType.getSimpleSlug()] =
        SchemaGenerator.fromType(delete.response.responseType)

      path.delete = delete.toPathOperation(pluginConfig)
    }

    spec.paths[pluginConfig.path] = path
  }

  private fun MethodInfo.toPathOperation(config: Config) = PathOperation(
    tags = config.tags.plus(this.tags),
    summary = this.summary,
    description = this.description,
    externalDocs = this.externalDocumentation,
    operationId = this.operationId,
    deprecated = this.deprecated,
    parameters = this.parameters,
    requestBody = when (this) {
      is DeleteInfo -> null
      is GetInfo -> null
      is PostInfo -> Request(
        description = this.request.description,
        content = mapOf(
          "application/json" to MediaType(
            schema = ReferenceSchema(this.request.requestType.getReferenceSlug())
          )
        ),
        required = true
      )
      is PutInfo -> Request(
        description = this.request.description,
        content = mapOf(
          "application/json" to MediaType(
            schema = ReferenceSchema(this.request.requestType.getReferenceSlug())
          )
        ),
        required = true
      )
    },
    responses = mapOf(
      this.response.responseCode.value to Response(
        description = this.response.description,
        content = mapOf(
          "application/json" to MediaType(
            schema = ReferenceSchema(this.response.responseType.getReferenceSlug())
          )
        )
      )
    )
  )
}
