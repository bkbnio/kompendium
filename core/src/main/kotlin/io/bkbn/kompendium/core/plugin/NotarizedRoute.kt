package io.bkbn.kompendium.core.plugin

import io.bkbn.kompendium.core.attribute.KompendiumAttributes
import io.bkbn.kompendium.core.metadata.DeleteInfo
import io.bkbn.kompendium.core.metadata.GetInfo
import io.bkbn.kompendium.core.metadata.HeadInfo
import io.bkbn.kompendium.core.metadata.MethodInfo
import io.bkbn.kompendium.core.metadata.PatchInfo
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
import kotlin.reflect.KClass
import kotlin.reflect.KType

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
    var patch: PatchInfo? = null
    var head: HeadInfo? = null
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
      SchemaGenerator.fromType(get.response.responseType)?.let { schema ->
        spec.components.schemas[get.response.responseType.getSimpleSlug()] = schema
      }
      path.get = get.toPathOperation(pluginConfig)
    }

    pluginConfig.post?.let { post ->
      SchemaGenerator.fromType(post.response.responseType)?.let { schema ->
        spec.components.schemas[post.response.responseType.getSimpleSlug()] = schema
      }
      SchemaGenerator.fromType(post.request.requestType)?.let { schema ->
        spec.components.schemas[post.request.requestType.getSimpleSlug()] = schema
      }
      path.post = post.toPathOperation(pluginConfig)
    }

    pluginConfig.put?.let { put ->
      SchemaGenerator.fromType(put.response.responseType)?.let { schema ->
        spec.components.schemas[put.response.responseType.getSimpleSlug()] = schema
      }
      SchemaGenerator.fromType(put.request.requestType)?.let { schema ->
        spec.components.schemas[put.request.requestType.getSimpleSlug()] = schema
      }
      path.put = put.toPathOperation(pluginConfig)
    }

    pluginConfig.delete?.let { delete ->
      SchemaGenerator.fromType(delete.response.responseType)?.let { schema ->
        spec.components.schemas[delete.response.responseType.getSimpleSlug()] = schema
      }

      path.delete = delete.toPathOperation(pluginConfig)
    }

    pluginConfig.patch?.let { patch ->
      SchemaGenerator.fromType(patch.response.responseType)?.let { schema ->
        spec.components.schemas[patch.response.responseType.getSimpleSlug()] = schema
      }
      SchemaGenerator.fromType(patch.request.requestType)?.let { schema ->
        spec.components.schemas[patch.request.requestType.getSimpleSlug()] = schema
      }
      path.patch = patch.toPathOperation(pluginConfig)
    }

    pluginConfig.head?.let { head ->
      SchemaGenerator.fromType(head.response.responseType)?.let { schema ->
        spec.components.schemas[head.response.responseType.getSimpleSlug()] = schema
      }

      path.head = head.toPathOperation(pluginConfig)
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
      is HeadInfo -> null
      is PatchInfo -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(),
        required = true
      )
      is PostInfo -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(),
        required = true
      )
      is PutInfo -> Request(
        description = this.request.description,
        content = this.request.requestType.toReferenceContent(),
        required = true
      )
    },
    responses = mapOf(
      this.response.responseCode.value to Response(
        description = this.response.description,
        content = this.response.responseType.toReferenceContent()
      )
    )
  )

  private fun KType.toReferenceContent(): Map<String, MediaType>? = when (this.classifier as KClass<*>) {
    Unit::class -> null
    else -> mapOf(
      "application/json" to MediaType(
        schema = ReferenceSchema(this.getReferenceSlug())
      )
    )
  }
}
